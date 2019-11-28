package com.qihoo.finance.chronus.executor.processor;

import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.common.util.NetUtils;
import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.dispatcher.TaskSelectTaskService;
import com.qihoo.finance.chronus.executor.bo.ProcessorParam;
import com.qihoo.finance.chronus.executor.service.JobExecLogSaveService;
import com.qihoo.finance.chronus.executor.service.ScheduleProcessor;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by xiongpu on 2019/8/16.
 */
@Slf4j
public abstract class AbstractProcessor<T> implements ScheduleProcessor {
    protected List<T> taskList = new CopyOnWriteArrayList<>();
    private ProcessorParam param;
    /**
     * 是否已经获得终止调度信号
     * 用户停止队列调度
     */
    protected volatile boolean isStopSchedule = false;
    /**
     * 调度周期结束
     */
    protected volatile boolean isExecutorEnd = false;

    protected ExecutorService taskExecThreadPool;

    protected boolean isBatchExecTask = false;

    private JobExecLogSaveService jobExecLogSaveService;


    /**
     * 创建一个调度处理器
     *
     * @param processorParam
     * @throws Exception
     */
    public AbstractProcessor(ProcessorParam processorParam) {
        this.param = processorParam;
        this.taskExecThreadPool = Executors.newFixedThreadPool(processorParam.getTaskEntity().getThreadNumber(), new ThreadFactory(processorParam.getTaskEntity()));
        this.jobExecLogSaveService = SpringContextHolder.getBean("jobExecLogSaveService");
        if (processorParam.getTaskEntity().getExecuteNumber() > 1) {
            this.isBatchExecTask = true;
        }
        this.startThread(processorParam);
    }

    /**
     * 需要注意的是，调度服务器从配置中心注销的工作，必须在所有线程退出的情况下才能做
     *
     * @throws Exception
     */
    @Override
    public void stopProcessor() {
        log.debug("stopProcessor! {}", this.param);
        this.isStopSchedule = true;
        this.taskList.clear();
    }


    public boolean isStop() {
        return this.isStopSchedule;
    }

    protected synchronized void executorEnded() {
        submitRuntimeState(ScheduleServerStatusEnum.pause);
        submitRuntimeMessage("本次调度周期执行完成!");
        this.isExecutorEnd = true;
    }

    @Override
    public boolean isExecutorEnd() {
        return this.isExecutorEnd;
    }

    /**
     * 启动调度工作线程
     * @param processorParam
     */
    protected abstract void startThread(ProcessorParam processorParam);

    public synchronized Object getScheduleTaskId() {
        if (this.taskList.size() > 0) {
            if (this.isBatchExecTask) {
                int size = taskList.size() > this.param.getTaskEntity().getExecuteNumber() ? this.param.getTaskEntity().getExecuteNumber() : taskList.size();
                Object[] result = null;
                if (size > 0) {
                    result = (Object[]) Array.newInstance(this.taskList.get(0).getClass(), size);
                }
                for (int i = 0; i < size; i++) {
                    // 按正序处理
                    result[i] = this.taskList.remove(0);
                }
                return result;
            } else {
                return this.taskList.remove(0);
            }
        }
        return null;
    }

    protected int loadScheduleData() {
        try {
            List<T> tmpList = ((TaskSelectTaskService<T>) param.getTaskDealBean()).selectTasks(param.getTaskEntity().getTaskParameter(), param.getTaskItems(), param.getTaskEntity().getFetchDataNumber());
            submitRuntimeLastFetchDataTime();
            if (tmpList != null) {
                this.taskList.addAll(tmpList);
            }
            return this.taskList.size();
        } catch (Throwable ex2) {
            log.error("Get tasks error.", ex2);
        }
        return 0;
    }

    protected List<Object> getTaskList() {
        List<Object> taskList = new ArrayList<>();
        while (true) {
            Object executeTask = this.getScheduleTaskId();
            if (this.isStop() || executeTask == null) {
                break;
            }
            taskList.add(executeTask);
        }
        return taskList;
    }

    protected void initContext(String requestNo, AtomicLong totalCount) {
        ServiceContext ctx = ServiceContext.getContext();
        ctx.clearContextVar();
        ctx.setRequestNo(String.format(requestNo + "#%s", totalCount.intValue()));
        ctx.setLocalIp(NetUtils.getLocalIP());
        ctx.setSysCode(param.getTaskEntity().getDealSysCode());
        ctx.setBeanName(param.getTaskEntity().getDealBeanName());
        ContextLog4j2Util.addContext2ThreadContext(ctx);
    }

    protected void sendExecLogMessage(String requestNo, Date startDate, Date endDate, AtomicLong totalCount, AtomicLong failCount) {
        JobExecLogEntity jobExecLogEntity = new JobExecLogEntity();
        jobExecLogEntity.setCluster(param.getTaskEntity().getCluster());
        jobExecLogEntity.setSysCode(param.getTaskEntity().getDealSysCode());
        jobExecLogEntity.setTaskName(param.getTaskEntity().getTaskName());
        jobExecLogEntity.setExecAddress(NetUtils.getLocalIP());
        jobExecLogEntity.setStartDate(startDate);
        jobExecLogEntity.setEndDate(endDate);
        jobExecLogEntity.setReqNo(String.format(requestNo + "#%s", 0));
        jobExecLogEntity.setDateCreated(new Date());
        jobExecLogEntity.setHandleTotalCount(totalCount.longValue());
        jobExecLogEntity.setHandleFailCount(failCount.longValue());
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
            Thread.sleep(millis);
        } catch (InterruptedException e) {
        }
    }


    protected void submitRuntimeState(ScheduleServerStatusEnum scheduleServerStatusEnum){
        this.param.getTaskRuntime().setState(scheduleServerStatusEnum.toString());
    }
    protected void submitRuntimeMessage(String message){
        this.param.getTaskRuntime().setMessage(message);
    }

    protected void submitRuntimeLastFetchDataTime(){
        this.param.getTaskRuntime().setLastFetchDataTime(DateUtils.now());
    }
}
