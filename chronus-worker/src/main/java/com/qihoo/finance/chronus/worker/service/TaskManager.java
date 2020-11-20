package com.qihoo.finance.chronus.worker.service;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import org.quartz.SchedulerException;

import java.util.Date;

/**
 * Created by xiongpu on 2019/8/3.
 */
public interface TaskManager {

    void start() throws SchedulerException;

    TaskRuntimeEntity getTaskRuntimeInfo();

    void setTaskNextRunTime(Date nextTime);

    /**
     * 守护线程调用
     * 启动一个processor  如果processor 正在处理中 则跳过这次启动
     */
    boolean startProcessor();

    /**
     * 守护线程调用-停止处理
     *
     * @return
     */
    boolean stopProcessor();

    /**
     * 任务分配结果调用
     * stop taskManager & stop processor
     * 停止心跳线程 守护任务 发送最后一次心跳信息
     */
    void stop();

    /**
     * 注册守护线程任务
     */
    void registerDaemonTask() throws SchedulerException;

    /**
     * 取消守护线程任务
     */
    void unRegisterDaemonTask();
}
