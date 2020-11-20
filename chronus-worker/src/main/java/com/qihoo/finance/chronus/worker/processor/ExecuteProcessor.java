package com.qihoo.finance.chronus.worker.processor;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ServiceContextHelper;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * 直接调用执行方法
 *
 * @param
 * @author xiongpu
 */
@Slf4j
public class ExecuteProcessor extends AbstractProcessor {

    @Override
    public void run() {
        String requestNo = getRequestNo();
        Date startDate = DateUtils.now();
        try {
            ServiceContextHelper.initContext(currentNode, processorParam.getTaskEntity(), requestNo, null);
            submitRuntimeState(TaskRunStatusEnum.RUNNING, null);
            processorParam.getJobDispatcher().execute();
        } catch (Throwable e) {
            log.error("Task :{} 处理失败", processorParam.getTaskItemEntity(), e);
            submitRuntimeState(TaskRunStatusEnum.ERROR, e.getMessage());
        } finally {
            sendExecLogMessage(requestNo, startDate, DateUtils.now(), null, null);
            this.executorEnded();
        }
    }
}

