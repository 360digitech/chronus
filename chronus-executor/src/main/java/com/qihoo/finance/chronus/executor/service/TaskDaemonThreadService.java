package com.qihoo.finance.chronus.executor.service;

import com.qihoo.finance.chronus.executor.service.impl.PauseOrResumeScheduleTask;

import java.util.Date;
import java.util.concurrent.ScheduledFuture;

/**
 * Created by xiongpu on 2019/8/28.
 */
public interface TaskDaemonThreadService {

    /**
     * 初始化守护线程任务
     * 将PauseOrResumeScheduleTask.key 维护到集合内
     * @param scheduleTask
     * @param nextTime
     */
    void initSchedule(PauseOrResumeScheduleTask scheduleTask, Date nextTime);

    /**
     * 如果当前key存在集合内则
     * 按照时间 指定下次任务什么时候开始
     * @param scheduleTask
     * @param nextTime
     * @return
     */
    ScheduledFuture schedule(PauseOrResumeScheduleTask scheduleTask, Date nextTime);

    /**
     * 从集合内移除 如果存在ScheduledFuture 则取消这个ScheduledFuture
     * @param scheduleTask
     */
    void cancelSchedule(PauseOrResumeScheduleTask scheduleTask);
}
