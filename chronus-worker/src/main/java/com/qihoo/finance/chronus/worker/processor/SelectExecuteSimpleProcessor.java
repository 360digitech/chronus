package com.qihoo.finance.chronus.worker.processor;

import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ServiceContextHelper;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import com.qihoo.finance.chronus.worker.bo.ProcessorParam;
import com.qihoo.finance.chronus.worker.service.ScheduleProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务调度器
 * 1.执行selectTasks加载数据
 * 2.执行execute处理数据
 * 按cron表达式调度
 * 每次处理完休眠时间会生效
 */
@Slf4j
public class SelectExecuteSimpleProcessor extends AbstractSelectExecuteProcessor {
    protected ExecutorService taskExecThreadPool;

    @Override
    public ScheduleProcessor init(ProcessorParam processorParam) {
        super.init(processorParam);
        this.taskExecThreadPool = Executors.newFixedThreadPool(processorParam.getTaskEntity().getThreadNumber(), new ThreadFactory(processorParam.getTaskItemEntity()));
        return this;
    }

    @Override
    public void run() {
        String requestNo = getRequestNo();
        AtomicLong totalCount = new AtomicLong();
        AtomicLong failCount = new AtomicLong();
        Date startDate = DateUtils.now();
        try {
            ServiceContextHelper.initContext(currentNode, processorParam.getTaskEntity(), requestNo, totalCount);
            submitRuntimeState(TaskRunStatusEnum.RUNNING, null);
            int size = this.loadScheduleData();
            if (this.isStop() || size == 0) {
                return;
            }
            List<List> taskList = getTaskList();
            CountDownLatch cdl = new CountDownLatch(taskList.size());
            for (List executeTask : taskList) {
                taskExecThreadPool.submit(() -> {
                    try {
                        if (this.isStop()) {
                            return null;
                        }
                        totalCount.incrementAndGet();
                        ServiceContextHelper.initContext(currentNode, processorParam.getTaskEntity(), requestNo, totalCount);
                        boolean result = processorParam.getJobDispatcher().execute(executeTask);
                        if (!result) {
                            failCount.incrementAndGet();
                        }
                        return result;
                    } catch (Throwable ex) {
                        failCount.incrementAndGet();
                        log.error("Task:{} {}处理失败", processorParam.getTaskItemEntity().getTaskItemId(), executeTask, ex);
                        return false;
                    } finally {
                        cdl.countDown();
                    }
                });
            }
            cdl.await();
            //在每次数据处理完毕后休眠固定的时间
            if (processorParam.getTaskEntity().getSleepTimeInterval() > 0 && !isStop()) {
                super.sleep((int) (processorParam.getTaskEntity().getSleepTimeInterval() * 1000));
            }
        } catch (Throwable e) {
            log.error("Task :{} 处理失败", processorParam.getTaskEntity(), e);
            submitRuntimeState(TaskRunStatusEnum.ERROR, e.getMessage());
        } finally {
            this.executorEnded();
            sendExecLogMessage(requestNo, startDate, DateUtils.now(), totalCount, failCount);
        }
    }
}

