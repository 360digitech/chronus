package com.qihoo.finance.chronus.worker.service.impl;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import com.qihoo.finance.chronus.protocol.api.ChronusSdkFacadeFactory;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;
import com.qihoo.finance.chronus.worker.bo.ProcessorParam;
import com.qihoo.finance.chronus.worker.processor.ProcessorFactory;
import com.qihoo.finance.chronus.worker.service.JobDispatcher;
import com.qihoo.finance.chronus.worker.service.ScheduleProcessor;
import com.qihoo.finance.chronus.worker.service.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by xiongpu on 2019/8/3.
 */
@Slf4j
public class TaskManagerImpl implements TaskManager {
    private static Pattern SCHEDULE_TASK_ITEM_REG = Pattern.compile("\\s*:\\s*");
    private static SchedulerFactory schedulerFactory = new StdSchedulerFactory();
    /**
     * 停止调度
     */
    protected volatile boolean isStopSchedule = false;

    private ScheduleProcessor processor;
    private volatile TaskEntity taskEntity;
    private volatile TaskRuntimeEntity taskRuntime;
    private volatile TaskItemEntity taskItemEntity;
    private static Scheduler scheduler;

    static {
        try {
            scheduler = schedulerFactory.getScheduler();
            scheduler.start();
        } catch (SchedulerException e) {
            log.error("schedulerFactory start error", e);
        }
    }

    /**
     * 队列处理器(实际调度任务的处理bean)
     */
    private JobDispatcher jobDispatcher;

    public TaskManagerImpl(TaskEntity taskEntity, TaskItemEntity taskItemEntity, TaskRuntimeEntity taskRuntime) throws Exception {
        this.taskEntity = taskEntity;
        this.taskRuntime = taskRuntime;
        this.taskItemEntity = taskItemEntity;
        this.checkBeforeCreating(this.taskEntity);
        this.jobDispatcher = initJobDispatcher();
        this.processor = ProcessorFactory.create(this.taskEntity.getProcessorType());

        ProcessorParam processorParam = ProcessorParam.builder()
                .jobDispatcher(this.jobDispatcher)
                .taskEntity(this.taskEntity)
                .taskItemEntity(this.taskItemEntity)
                .taskRuntime(this.taskRuntime)
                .isExecutorEnd(true)
                .isStopSchedule(false)
                .build();
        this.processor.init(processorParam);
    }

    private void checkBeforeCreating(TaskEntity taskEntity) {
        if (StringUtils.isNotBlank(taskEntity.getPermitRunStartTime())) {
            try {
                CronExpression.validateExpression(taskEntity.getPermitRunStartTime());
            } catch (Exception e) {
                throw new RuntimeException("解析Corn表达式异常! cornExpression:[" + taskEntity.getPermitRunStartTime() + "]" + e.getMessage());
            }
        }
    }

    /**
     * 重新获取taskDealBean重置变量
     *
     * @throws Exception
     */
    private JobDispatcher initJobDispatcher() {
        SystemGroupService systemGroupService = SpringContextHolder.getBean(SystemGroupService.class);
        SystemGroupEntity systemGroupEntity = systemGroupService.loadSystemGroupBySysCode(this.taskEntity.getDealSysCode());
        ChronusSdkFacadeFactory chronusSdkFacadeFactory = SpringContextHolder.getBean(ChronusConstants.CHRONUS_SDK_FACADE_FACTORY + "#" + StringUtils.defaultString(systemGroupEntity.getProtocolType(), ChronusConstants.DEFAULT_SDK_FACTORY));

        ChronusSdkFacade chronusSdkFacade = chronusSdkFacadeFactory.getInstance(systemGroupEntity);

        JobData jobData = new JobData();
        jobData.setServiceName(this.taskEntity.getDealSysCode());
        jobData.setBeanName(this.taskEntity.getDealBizBeanName());
        jobData.setEachFetchDataNum(this.taskEntity.getFetchDataNumber());
        jobData.setTaskItemList(getTaskItemDefineList(taskItemEntity.getTaskItems()));
        jobData.setTaskName(this.taskEntity.getTaskName());
        jobData.setTaskParameter(this.taskEntity.getTaskParameter());

        JobDispatcher jobDispatcher = SpringContextHolder.getBean(JobDispatcher.class);
        jobDispatcher.init(chronusSdkFacade, jobData);
        return jobDispatcher;
    }

    @Override
    public synchronized void start() throws SchedulerException {
        this.registerDaemonTask();
    }

    @Override
    public TaskRuntimeEntity getTaskRuntimeInfo() {
        return BeanUtils.copyBean(TaskRuntimeEntity.class, this.taskRuntime);
    }

    @Override
    public void setTaskNextRunTime(Date nextTime) {
        this.taskRuntime.setNextRunStartTime(nextTime);
    }

    @Override
    public synchronized boolean startProcessor() {
        if (this.isStop()) {
            return false;
        }
        if (this.processor.isExecutorEnd()) {
            this.processor.start().startThread();
            return true;
        } else {
            log.debug("调度运行中,跳过");
            return false;
        }
    }

    @Override
    public boolean stopProcessor() {
        if (this.processor != null) {
            this.taskRuntime.setState(TaskRunStatusEnum.SLEEP.toString());
            this.processor.shutdown();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void stop() {
        this.isStopSchedule = true;
        if (this.processor != null) {
            this.processor.shutdown();
        }
        this.unRegisterDaemonTask();
        log.info("TaskName:{} SeqNo:{} taskItems:{} 已停止!", this.taskItemEntity.getTaskName(), this.taskItemEntity.getSeqNo(), this.taskItemEntity.getTaskItems());
    }

    private boolean isStop() {
        return this.isStopSchedule;
    }

    @Override
    public void registerDaemonTask() throws SchedulerException {
        // 2、创建JobDetail实例，并与PrintWordsJob类绑定(Job执行内容)
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put("taskManager", this);
        JobDetail jobDetail = JobBuilder.newJob(DaemonJob.class)
                .usingJobData(jobDataMap)
                .withIdentity(this.taskItemEntity.getTaskItemId(), this.taskEntity.getDealSysCode()).build();
        Trigger cronTrigger = TriggerBuilder.newTrigger().withIdentity(this.taskItemEntity.getTaskItemId(), this.taskEntity.getDealSysCode())
                .startNow()//立即生效
                .withSchedule(CronScheduleBuilder.cronSchedule(this.taskEntity.getPermitRunStartTime()))
                .build();

        //4、执行
        scheduler.scheduleJob(jobDetail, cronTrigger);

        this.taskRuntime.setNextRunStartTime(scheduler.getTrigger(cronTrigger.getKey()).getNextFireTime());
    }

    @Override
    public void unRegisterDaemonTask() {
        try {
            TriggerKey triggerKey = TriggerKey.triggerKey(this.taskItemEntity.getTaskItemId(), this.taskEntity.getDealSysCode());
            scheduler.pauseTrigger(triggerKey);//停止触发器
            scheduler.unscheduleJob(triggerKey);//移除触发器
            scheduler.deleteJob(JobKey.jobKey(this.taskItemEntity.getTaskItemId(), this.taskEntity.getDealSysCode()));
        } catch (SchedulerException e) {
            log.error("schedulerFactory deleteJob error", e);
        }
    }

    public static List<TaskItemDefineDomain> getTaskItemDefineList(String taskItemsStr) {
        List<TaskItemDefineDomain> taskItemsResult = new ArrayList<>();
        if (StringUtils.isBlank(taskItemsStr)) {
            return taskItemsResult;
        }
        List<String> taskItems = Arrays.asList(taskItemsStr.split(","));
        for (int i = 0; i < taskItems.size(); i++) {
            TaskItemDefineDomain taskItemDefine = new TaskItemDefineDomain();
            String ti = taskItems.get(i);
            if (ti == null) {
                taskItemsResult.add(null);
                continue;
            }
            Matcher matcher = SCHEDULE_TASK_ITEM_REG.matcher(ti);
            if (matcher.find()) {
                taskItemDefine.setTaskItemId(ti.substring(0, matcher.start()).trim());
                taskItemDefine.setParameter(ti.substring(matcher.end(), ti.length() - 1).trim());
            } else {
                taskItemDefine.setTaskItemId(ti);
            }
            taskItemsResult.add(taskItemDefine);
        }
        return taskItemsResult;
    }
}
