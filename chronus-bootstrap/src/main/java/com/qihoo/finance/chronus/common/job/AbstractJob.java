package com.qihoo.finance.chronus.common.job;

import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.context.ServiceContext;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.CronTrigger;

import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象job类。
 */
public abstract class AbstractJob implements SchedulingConfigurer {

    protected volatile AtomicBoolean run = new AtomicBoolean(true);

    /**
     * job 运行入口
     */
    public void run() {
        if (run.get()) {
            run.set(false);
            ServiceContext.getContext().clearContextVar().setRequestNo(ServiceContext.genUniqueId());
            ContextLog4j2Util.addContext2ThreadContext();
            execute();
            ContextLog4j2Util.removeContextFromThreadContext();
            run.set(true);
        }
    }


    protected void configureTasks(ScheduledTaskRegistrar taskRegistrar, Runnable task, String cron) {
        taskRegistrar.addTriggerTask(task, (TriggerContext triggerContext) -> {
            CronTrigger trigger = new CronTrigger(cron);
            Date nextExec = trigger.nextExecutionTime(triggerContext);
            return nextExec;
        });
    }

    /**
     * 定时任务具体逻辑
     */
    protected abstract void execute();
}
