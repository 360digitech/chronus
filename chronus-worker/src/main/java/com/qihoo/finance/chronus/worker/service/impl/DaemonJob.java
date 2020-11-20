package com.qihoo.finance.chronus.worker.service.impl;

import com.qihoo.finance.chronus.context.ServiceContextHelper;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.worker.service.TaskManager;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;

import java.util.Date;

/**
 * 守护任务 按规则 定时唤起任务处理
 */
@Slf4j
@DisallowConcurrentExecution
public class DaemonJob implements Job {
    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        Date nextTime = jobExecutionContext.getTrigger().getNextFireTime();
        TaskManager taskManager = (TaskManager) jobExecutionContext.getJobDetail().getJobDataMap().get("taskManager");
        TaskRuntimeEntity taskRuntime = taskManager.getTaskRuntimeInfo();
        Thread.currentThread().setName(taskRuntime.getTaskItemId());

        ServiceContextHelper.initContext(taskRuntime);
        boolean startResult = taskManager.startProcessor();
        if (startResult) {
            taskManager.setTaskNextRunTime(nextTime);
            log.debug("到达开始时间,恢复调度,下次触发时间:{}", nextTime);
        }
    }
}
