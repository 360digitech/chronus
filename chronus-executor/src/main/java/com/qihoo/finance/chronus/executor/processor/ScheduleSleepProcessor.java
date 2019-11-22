package com.qihoo.finance.chronus.executor.processor;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.dispatcher.TaskBatchDealService;
import com.qihoo.finance.chronus.dispatcher.TaskSingleDealService;
import com.qihoo.finance.chronus.executor.bo.ProcessorParam;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务调度器，在Manager的管理下实现多线程数据处理
 */
@Slf4j
public class ScheduleSleepProcessor extends AbstractProcessor {

    /**
     * 创建一个调度处理器
     *
     * @param processorParam
     * @throws Exception
     */
    public ScheduleSleepProcessor(ProcessorParam processorParam) {
        super(processorParam);
    }

    @Override
    protected void startThread(ProcessorParam processorParam) {
        Thread mainThread = new Thread(() -> {
            try {
                while (!this.isStop()) {
                    String requestNo = ServiceContext.genUniqueId();

                    AtomicLong totalCount = new AtomicLong();
                    AtomicLong failCount = new AtomicLong();
                    initContext(requestNo, totalCount);
                    Date startDate = DateUtils.now();
                    submitRuntimeState(ScheduleServerStatusEnum.normal);
                    int size = this.loadScheduleData();
                    if (this.isStop()) {
                        this.taskExecThreadPool.shutdown();
                        sendExecLogMessage(requestNo, startDate, DateUtils.now(), totalCount, failCount);
                        break;
                    }
                    if (size == 0) {
                        // 没有数据休眠
                        sendExecLogMessage(requestNo, startDate, DateUtils.now(), totalCount, failCount);
                        if (processorParam.getTaskEntity().getSleepTimeNoData() > 0 && !isStop()) {
                            Thread.sleep((int) (processorParam.getTaskEntity().getSleepTimeNoData() * 1000));
                        }
                        break;
                    } else {
                        List<Object> taskList = getTaskList();
                        CountDownLatch cdl = new CountDownLatch(taskList.size());
                        for (Object executeTask : taskList) {
                            taskExecThreadPool.submit(() -> {
                                totalCount.incrementAndGet();
                                initContext(requestNo, totalCount);
                                try {
                                    if (this.isStop()) {
                                        return null;
                                    }
                                    boolean result;
                                    if (this.isBatchExecTask) {
                                        result = ((TaskBatchDealService) processorParam.getTaskDealBean()).execute((Object[]) executeTask);
                                    } else {
                                        result = ((TaskSingleDealService) processorParam.getTaskDealBean()).execute(executeTask);
                                    }
                                    if (!result) {
                                        failCount.incrementAndGet();
                                    }
                                    return result;
                                } catch (Throwable ex) {
                                    failCount.incrementAndGet();
                                    log.error("Task :" + executeTask + " 处理失败", ex);
                                    return false;
                                } finally {
                                    cdl.countDown();
                                }
                            });
                        }
                        cdl.await();
                        sendExecLogMessage(requestNo, startDate, DateUtils.now(), totalCount, failCount);
                        //在每次数据处理完毕后休眠固定的时间
                        if (processorParam.getTaskEntity().getSleepTimeInterval() > 0 && !isStop()) {
                            Thread.sleep((int) (processorParam.getTaskEntity().getSleepTimeInterval() * 1000));
                        }
                    }
                    if (ChronusConstants.Y.equals(processorParam.getTaskEntity().getForceCronExec())) {
                        break;
                    }
                }
                log.debug("暂停{}-{}调度  currentThread:{}", processorParam.getTaskEntity().getDealSysCode(), processorParam.getTaskEntity().getTaskName(), Thread.currentThread().getName());
                if (!taskExecThreadPool.isShutdown()) {
                    taskExecThreadPool.shutdown();
                }
                while (!taskExecThreadPool.isTerminated()) {
                    sleep(500);
                }
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            } finally {
                if (!taskExecThreadPool.isShutdown()) {
                    taskExecThreadPool.shutdown();
                }
                this.executorEnded();
            }
        });
        String threadName = processorParam.getTaskEntity().getDealSysCode() + "-" + processorParam.getTaskEntity().getTaskName() + "-exe-main";
        mainThread.setName(threadName);
        mainThread.start();
    }


}

