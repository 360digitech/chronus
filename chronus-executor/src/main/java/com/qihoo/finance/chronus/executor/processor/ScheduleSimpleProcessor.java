package com.qihoo.finance.chronus.executor.processor;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.dispatcher.TaskSimpleDealService;
import com.qihoo.finance.chronus.executor.bo.ProcessorParam;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 任务调度器
 *
 * @param
 * @author xiongpu
 */
@Slf4j
public class ScheduleSimpleProcessor extends AbstractProcessor {

    /**
     * 创建一个调度处理器
     *
     * @param processorParam
     * @throws Exception
     */
    public ScheduleSimpleProcessor(ProcessorParam processorParam) {
        super(processorParam);
    }

    @Override
    protected void startThread(ProcessorParam processorParam) {
        Thread mainThread = new Thread(() -> {
            try {
                String requestNo = ServiceContext.genUniqueId();
                AtomicLong totalCount = new AtomicLong();
                AtomicLong failCount = new AtomicLong();
                initContext(requestNo, totalCount);
                Date startDate = DateUtils.now();
                submitRuntimeState(ScheduleServerStatusEnum.normal);
                try {
                    TaskEntity taskEntity = processorParam.getTaskEntity();
                    TaskSimpleDealService taskSimpleDealService = ((TaskSimpleDealService) processorParam.getTaskDealBean());
                    submitRuntimeLastFetchDataTime();
                    boolean result = taskSimpleDealService.execute(taskEntity.getTaskParameter(), processorParam.getTaskItems(), taskEntity.getFetchDataNumber());
                    totalCount.incrementAndGet();
                    if (!result) {
                        failCount.incrementAndGet();
                    }
                } catch (Throwable ex) {
                    failCount.incrementAndGet();
                    log.error("Task :{} 处理失败", processorParam.getTaskEntity(), ex);
                    return;
                }
                sendExecLogMessage(requestNo, startDate, DateUtils.now(), totalCount, failCount);
            } catch (Throwable e) {
                log.error(e.getMessage(), e);
            } finally {
                this.executorEnded();
            }
        });
        String threadName = processorParam.getTaskEntity().getDealSysCode() + "-" + processorParam.getTaskEntity().getTaskName() + "-exe-main";
        mainThread.setName(threadName);
        mainThread.start();
    }
}

