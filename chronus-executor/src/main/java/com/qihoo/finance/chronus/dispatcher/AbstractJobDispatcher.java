package com.qihoo.finance.chronus.dispatcher;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.core.log.LogUtil;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import org.apache.dubbo.rpc.RpcContext;
import org.apache.logging.log4j.Logger;
import org.springframework.util.StopWatch;

import static com.qihoo.finance.chronus.common.ChronusConstants.MONITOR_LOGGER_PATTERN_EXECUTE;
import static com.qihoo.finance.chronus.common.ChronusConstants.MONITOR_LOGGER_PATTERN_SELECT_TASKS;

/**
 * Created by xiongpu on 2019/1/9.
 */
public abstract class AbstractJobDispatcher implements TaskDealService {

    protected TaskEntity taskTypeInfo;

    protected JobConfig jobConfig;

    protected Logger getLogger() {
        return LogUtil.getLogger(jobConfig.getSysCode());
    }

    protected Logger getMonitorLogger() {
        return LogUtil.getMonitorLogger(jobConfig.getSysCode());
    }

    protected StopWatch getStopWatchAndStart() {
        StopWatch clock = new StopWatch();
        clock.start();
        return clock;
    }

    protected void printExecMonitorLog(StopWatch clock, boolean resultFlag) {
        clock.stop();
        getMonitorLogger().info(MONITOR_LOGGER_PATTERN_EXECUTE,
                jobConfig.getSysCode(),
                jobConfig.getTaskName(),
                jobConfig.getBeanName(),
                ContextLog4j2Util.getRequestNo(),
                RpcContext.getContext().getRemoteHost(),
                resultFlag ? Response.SUCC : Response.FAIL, clock.getTotalTimeMillis());
    }

    protected void printSelectTaskMonitorLog(StopWatch clock, int size, boolean resultFlag) {
        clock.stop();
        getMonitorLogger().info(MONITOR_LOGGER_PATTERN_SELECT_TASKS,
                jobConfig.getSysCode(),
                jobConfig.getTaskName(),
                jobConfig.getBeanName(),
                ContextLog4j2Util.getRequestNo(),
                RpcContext.getContext().getRemoteHost(),
                size,
                resultFlag ? Response.SUCC : Response.FAIL, clock.getTotalTimeMillis());
    }

    @Override
    public void setTaskInfo(TaskEntity taskTypeInfo) {
        this.taskTypeInfo = taskTypeInfo;
        this.jobConfig = new JobConfig(taskTypeInfo.getDealSysCode(), taskTypeInfo.getTaskName(), taskTypeInfo.getDealBizBeanName(), taskTypeInfo.getTaskParameter(), taskTypeInfo.getIsBroadcastInvoker());
    }
}
