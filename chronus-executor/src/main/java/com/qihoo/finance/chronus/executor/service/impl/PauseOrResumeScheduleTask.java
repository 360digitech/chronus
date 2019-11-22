package com.qihoo.finance.chronus.executor.service.impl;

import com.qihoo.finance.chronus.common.util.CronUtils;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.executor.service.TaskDaemonThreadService;
import com.qihoo.finance.chronus.executor.service.TaskManager;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * Created by xiongpu on 2019/8/3.
 */
@Slf4j
public class PauseOrResumeScheduleTask implements Runnable {
    private String key;
    private TaskManager taskManager;
    private String cornExpression;
    private TaskRuntimeEntity taskRuntime;
    public static final int TYPE_PAUSE = 1;
    public static final int TYPE_RESUME = 2;
    private int type;

    public PauseOrResumeScheduleTask(TaskManager taskManager, String cornExpression, TaskRuntimeEntity taskRuntime, int type) {
        this.key = taskRuntime.getCluster() + "#" + taskRuntime.getTaskName() + "#" + taskRuntime.getSeqNo() + "#" + type;
        this.taskManager = taskManager;
        this.cornExpression = cornExpression;
        this.taskRuntime = taskRuntime;
        this.type = type;
    }

    @Override
    public void run() {
        Date now = DateUtils.now();
        try {
            Date nextTime = CronUtils.getNextDateAfterNow(this.cornExpression, now);
            if (this.type == TYPE_PAUSE) {
                boolean stopResult = taskManager.stopProcessor();
                this.taskRuntime.setNextRunEndTime(nextTime);
                if (stopResult) {
                    this.taskRuntime.setMessage("到达终止时间,暂停调度!");
                    log.debug("到达终止时间,pause调度!");
                }
            } else {
                boolean startResult = taskManager.startProcessor();
                this.taskRuntime.setNextRunStartTime(nextTime);
                log.debug("到达开始时间,resume调度,nextRunStartTime:{}", nextTime);
                if (startResult) {
                    this.taskRuntime.setMessage("到达开始时间,恢复调度!");
                }
            }
            TaskDaemonThreadService taskDaemonThreadService = SpringContextHolder.getBean("taskDaemonThreadService");
            taskDaemonThreadService.schedule(this, nextTime);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public String getKey() {
        return key;
    }
}
