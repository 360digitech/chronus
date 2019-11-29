package com.qihoo.finance.chronus.executor.service.impl;

import com.qihoo.finance.chronus.common.util.CronUtils;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.task.cache.TaskCache;
import com.qihoo.finance.chronus.dispatcher.TaskDealService;
import com.qihoo.finance.chronus.executor.bo.ProcessorParam;
import com.qihoo.finance.chronus.executor.processor.ProcessorFactory;
import com.qihoo.finance.chronus.executor.service.ScheduleProcessor;
import com.qihoo.finance.chronus.executor.service.TaskDaemonThreadService;
import com.qihoo.finance.chronus.executor.service.TaskHeartBeatService;
import com.qihoo.finance.chronus.executor.service.TaskManager;
import com.qihoo.finance.chronus.metadata.api.task.bo.TaskItemDefine;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;

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

    private TaskHeartBeatService taskHeartBeatService;
    private TaskDaemonThreadService taskDaemonThreadService;
    private PauseOrResumeScheduleTask resumeScheduleTask;
    private PauseOrResumeScheduleTask pauseScheduleTask;
    private List<TaskItemDefine> taskItems = new ArrayList<>();
    /**
     * 停止调度
     */
    protected volatile boolean isStopSchedule = false;

    private ScheduleProcessor processor;
    private volatile TaskEntity taskEntity;
    private volatile TaskRuntimeEntity taskRuntime;
    /**
     * 队列处理器(实际调度任务的处理bean)
     */
    private TaskDealService taskDealBean;

    public TaskManagerImpl(TaskEntity taskEntity, TaskRuntimeEntity taskRuntime) throws Exception {
        this.taskEntity = taskEntity;
        this.taskRuntime = taskRuntime;
        this.taskItems = getTaskItemDefineList(taskRuntime.getTaskItems());
        this.taskDealBean = initTaskDealBean();
    }

    /**
     * 重新获取taskDealBean重置变量
     *
     * @throws Exception
     */
    private TaskDealService initTaskDealBean() throws Exception {
        Object dealBean = null;
        try {
            dealBean = SpringContextHolder.getBean(this.taskEntity.getDealBeanName());
        } catch (Exception e) {
            log.error("获取调度处理Bean异常,请检查相关配置(任务Bean配置和调度系统相关组件是否有打开.)", e);
        }
        if (dealBean == null) {
            throw new Exception("SpringBean " + this.taskEntity.getDealBeanName() + " 不存在");
        }

        ((TaskDealService) dealBean).setTaskInfo(this.taskEntity);
        return (TaskDealService) dealBean;
    }


    private void refresh() {
        String cluster = this.taskEntity.getCluster();
        String taskName = this.taskEntity.getTaskName();

        this.taskEntity = TaskCache.getTaskByClusterAndName(cluster, taskName);
        Assert.notNull(this.taskEntity, "从缓存内获取任务信息失败! cluster:" + cluster + ",taskName:" + taskName);
    }

    @Override
    public synchronized boolean startProcessor() {
        if (this.isStop()) {
            return false;
        }
        this.refresh();
        if (this.processor == null || this.processor.isExecutorEnd()) {
            ProcessorParam processorParam = ProcessorParam.builder().taskManager(this).taskDealBean(this.taskDealBean).taskEntity(this.taskEntity).taskItems(this.taskItems).taskRuntime(this.taskRuntime).build();
            this.processor = ProcessorFactory.create(this.taskEntity.getProcessorType(), processorParam);
            return true;
        } else {
            log.debug("调度运行中,跳过");
            return false;
        }
    }

    @Override
    public boolean stopProcessor() {
        if (this.processor != null) {
            this.taskRuntime.setState(ScheduleServerStatusEnum.pause.toString());
            this.processor.stopProcessor();
            return true;
        }
        return false;
    }

    @Override
    public synchronized void stop() {
        this.isStopSchedule = true;
        if (this.processor != null) {
            this.processor.stopProcessor();
        }
        this.unRegisterDaemonTask(this.taskDaemonThreadService);
        this.unRegisterHeartBeatTaskAndClearRuntimeInfo(this.taskHeartBeatService);
        log.info("TaskName:{} SeqNo:{} taskItems:{} 已停止!", this.taskRuntime.getTaskName(), this.taskRuntime.getSeqNo(), this.taskRuntime.getTaskItems());
    }

    private boolean isStop() {
        return this.isStopSchedule;
    }

    @Override
    public void registerHeartBeatTask(TaskHeartBeatService taskHeartBeatService) {
        this.taskHeartBeatService = taskHeartBeatService;
        this.taskHeartBeatService.addTaskToHeartBeatQueue(this.taskEntity.getHeartBeatRate(), this.taskRuntime);
    }

    @Override
    public void unRegisterHeartBeatTask(TaskHeartBeatService taskHeartBeatService) {
        this.taskHeartBeatService = taskHeartBeatService;
        this.taskHeartBeatService.removeTaskFromHeartBeatQueue(this.taskEntity.getHeartBeatRate(), this.taskRuntime,false);
    }

    private void unRegisterHeartBeatTaskAndClearRuntimeInfo(TaskHeartBeatService taskHeartBeatService) {
        this.taskHeartBeatService = taskHeartBeatService;
        this.taskHeartBeatService.removeTaskFromHeartBeatQueue(this.taskEntity.getHeartBeatRate(), this.taskRuntime,true);
    }

    @Override
    public void registerDaemonTask(TaskDaemonThreadService taskDaemonThreadService) {
        this.taskDaemonThreadService = taskDaemonThreadService;

        Date now = DateUtils.now();
        Date firstStartTime;
        try {
            firstStartTime = CronUtils.getNextDateAfterNow(taskEntity.getPermitRunStartTime(), now);
        } catch (Exception e) {
            this.taskRuntime.setState(ScheduleServerStatusEnum.error.name());
            this.taskRuntime.setMessage("解析Corn表达式异常! cornExpression:[" + taskEntity.getPermitRunStartTime() + "]" + e.getMessage());
            throw e;
        }
        this.taskRuntime.setNextRunStartTime(firstStartTime);
        this.resumeScheduleTask = new PauseOrResumeScheduleTask(this, taskEntity.getPermitRunStartTime(), taskRuntime, PauseOrResumeScheduleTask.TYPE_RESUME);
        this.taskDaemonThreadService.initSchedule(this.resumeScheduleTask, firstStartTime);

        if (StringUtils.isNotBlank(taskEntity.getPermitRunEndTime())) {
            Date firstEndTime = CronUtils.getNextDateAfterNow(taskEntity.getPermitRunEndTime(), firstStartTime);
            Date nowEndTime = CronUtils.getNextDateAfterNow(taskEntity.getPermitRunEndTime(), now);

            if (!nowEndTime.equals(firstEndTime) && now.before(nowEndTime)) {
                firstEndTime = nowEndTime;
            }
            this.pauseScheduleTask = new PauseOrResumeScheduleTask(this, taskEntity.getPermitRunEndTime(), taskRuntime, PauseOrResumeScheduleTask.TYPE_PAUSE);
            this.taskDaemonThreadService.schedule(this.pauseScheduleTask, firstEndTime);
            this.taskRuntime.setNextRunEndTime(firstEndTime);
        }
    }

    @Override
    public void unRegisterDaemonTask(TaskDaemonThreadService taskDaemonThreadService) {
        this.taskDaemonThreadService = taskDaemonThreadService;
        if (this.resumeScheduleTask != null) {
            this.taskDaemonThreadService.cancelSchedule(this.resumeScheduleTask);
        }
        if (this.pauseScheduleTask != null) {
            this.taskDaemonThreadService.cancelSchedule(this.pauseScheduleTask);
        }
    }

    public static List<TaskItemDefine> getTaskItemDefineList(String taskItemsStr) {
        List<TaskItemDefine> taskItemsResult = new ArrayList<>();
        if (StringUtils.isBlank(taskItemsStr)) {
            return taskItemsResult;
        }
        List<String> taskItems = Arrays.asList(taskItemsStr.split(","));
        for (int i = 0; i < taskItems.size(); i++) {
            TaskItemDefine taskItemDefine = new TaskItemDefine();
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
