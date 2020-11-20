package com.qihoo.finance.chronus.worker.processor;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import com.qihoo.finance.chronus.worker.bo.ProcessorParam;
import com.qihoo.finance.chronus.worker.service.JobExecLogSaveService;
import com.qihoo.finance.chronus.worker.service.ScheduleProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiongpu on 2019/8/16.
 */
@Slf4j
public abstract class AbstractProcessor implements ScheduleProcessor, Runnable {
    //公共任务提交线程池
    protected static ExecutorService commonThreadPool = Executors.newCachedThreadPool(new WorkerThreadFactory());

    protected ProcessorParam processorParam;

    private JobExecLogSaveService jobExecLogSaveService;
    protected NodeInfo currentNode;

    @Override
    public ScheduleProcessor init(ProcessorParam processorParam) {
        this.processorParam = processorParam;
        this.jobExecLogSaveService = SpringContextHolder.getBean(JobExecLogSaveService.class);
        this.currentNode = SpringContextHolder.getBean(NodeInfo.class);
        return this;
    }

    @Override
    public ScheduleProcessor start() {
        this.processorParam.setExecutorEnd(false);
        return this;
    }

    @Override
    public ProcessorParam getProcessorParam() {
        return this.processorParam;
    }

    @Override
    public void startThread() {
        commonThreadPool.submit(this);
    }

    protected String getRequestNo() {
        return ServiceContext.genUniqueId();
    }

    /**
     * 需要注意的是，调度服务器从配置中心注销的工作，必须在所有线程退出的情况下才能做
     *
     * @throws Exception
     */
    @Override
    public void shutdown() {
        log.debug("stopProcessor! {}", this.processorParam);
        processorParam.setStopSchedule(true);
    }

    public boolean isStop() {
        return processorParam.isStopSchedule();
    }

    protected synchronized void executorEnded() {
        submitRuntimeState(TaskRunStatusEnum.WAITING, "本次调度周期执行完成!");
        processorParam.setExecutorEnd(true);
    }

    @Override
    public boolean isExecutorEnd() {
        return processorParam.isExecutorEnd();
    }

    protected void sendExecLogMessage(String requestNo, Date startDate, Date endDate, AtomicLong totalCount, AtomicLong failCount) {
        JobExecLogEntity jobExecLogEntity = new JobExecLogEntity();
        jobExecLogEntity.setCluster(processorParam.getTaskEntity().getCluster());
        jobExecLogEntity.setSysCode(processorParam.getTaskEntity().getDealSysCode());
        jobExecLogEntity.setTaskName(processorParam.getTaskEntity().getTaskName());
        jobExecLogEntity.setExecAddress(currentNode.getAddress());
        jobExecLogEntity.setStartDate(startDate);
        jobExecLogEntity.setEndDate(endDate);
        jobExecLogEntity.setReqNo(String.format(requestNo + "#%s", 0));
        jobExecLogEntity.setDateCreated(new Date());
        jobExecLogEntity.setHandleTotalCount(totalCount != null ? totalCount.longValue() : null);
        jobExecLogEntity.setHandleFailCount(failCount != null ? failCount.longValue() : null);
        sendMessage(jobExecLogEntity);
    }

    protected void sendMessage(JobExecLogEntity jobExecLogEntity) {
        try {
            jobExecLogSaveService.sendExecLogToQueue(jobExecLogEntity);
        } catch (Exception e) {
            log.error("保存任务执行日志异常!", e);
        }
    }

    protected void sleep(long millis) {
        try {
            submitRuntimeState(TaskRunStatusEnum.SLEEP, "睡眠中");
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }

    protected void submitRuntimeState(TaskRunStatusEnum taskRunStatusEnum, String message) {
        if (TaskRunStatusEnum.RUNNING.name().equals(taskRunStatusEnum.name())) {
            this.processorParam.getTaskRuntime().setLastRunDataTime(DateUtils.now());
        }
        //如果本次调度出现异常, 保留异常消息
        if (TaskRunStatusEnum.isWaitingStatus(taskRunStatusEnum.name())) {
            if (!TaskRunStatusEnum.isErrorStatus(processorParam.getTaskRuntime().getState())) {
                this.processorParam.getTaskRuntime().setMessage(message);
            }
        } else {
            this.processorParam.getTaskRuntime().setMessage(message);
        }
        this.processorParam.getTaskRuntime().setState(taskRunStatusEnum.name());
        this.processorParam.getTaskRuntime().setDateUpdated(DateUtils.now());
    }
}
