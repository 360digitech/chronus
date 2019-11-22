package com.qihoo.finance.chronus.core.task.job;

import com.qihoo.finance.chronus.common.job.AbstractJob;
import com.qihoo.finance.chronus.common.ehcache.AbstractLocalCacheLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Created by xiongpu on 2019/8/23.
 */
@Component
public class TaskCacheJob extends AbstractJob{

    @Resource
    private AbstractLocalCacheLoader taskCache;

    @Value("${chronus.cache.task.cron:0 0/1 * * * ?}")
    private String taskCacheCron;


    @Override
    public void execute() {
        taskCache.update();
    }

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        configureTasks(taskRegistrar, () -> run(), taskCacheCron);
    }
}
