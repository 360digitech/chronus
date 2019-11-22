package com.qihoo.finance.chronus.executor.service.impl;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.executor.service.TaskDaemonThreadService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/8/28.
 */
public class TaskDaemonThreadServiceImpl implements TaskDaemonThreadService {
    /**
     * 任务的守护线程池
     *
     */
    private static ScheduledExecutorService daemonTaskTimer = Executors.newScheduledThreadPool(50);

    private static Map<String, ScheduledFuture> scheduledFutureMap = new HashMap<>();
    private Interner<String> pool = Interners.newWeakInterner();

    @Override
    public void initSchedule(PauseOrResumeScheduleTask scheduleTask, Date nextTime) {
        scheduledFutureMap.put(scheduleTask.getKey(), null);
        ScheduledFuture scheduledFuture = this.schedule(scheduleTask, nextTime);
        scheduledFutureMap.put(scheduleTask.getKey(), scheduledFuture);
    }

    @Override
    public ScheduledFuture schedule(PauseOrResumeScheduleTask scheduleTask, Date nextTime) {
        synchronized (pool.intern(scheduleTask.getKey())){
            if (scheduledFutureMap.containsKey(scheduleTask.getKey())) {
                return daemonTaskTimer.schedule(scheduleTask, (nextTime.getTime() - DateUtils.now().getTime()), TimeUnit.MILLISECONDS);
            }
        }
        return null;
    }

    @Override
    public void cancelSchedule(PauseOrResumeScheduleTask scheduleTask) {
        synchronized (pool.intern(scheduleTask.getKey())) {
            if (scheduledFutureMap.containsKey(scheduleTask.getKey())) {
                ScheduledFuture scheduledFuture = scheduledFutureMap.remove(scheduleTask.getKey());
                if (scheduledFuture != null) {
                    scheduledFuture.cancel(true);
                }
            }
        }
    }
}
