package com.qihoo.finance.chronus.executor.service;

/**
 * Created by xiongpu on 2019/8/3.
 */
public interface TaskManager {

    /**
     * 守护线程调用
     * 启动一个processor  如果processor 正在处理中 则跳过这次启动
     */
    boolean startProcessor();

    /**
     * 守护线程调用-停止处理
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
     * 注册心跳任务
     * @param taskHeartBeatService
     */
    void registerHeartBeatTask(TaskHeartBeatService taskHeartBeatService);

    /**
     * 取消心跳任务
     * @param taskHeartBeatService
     */
    void unRegisterHeartBeatTask(TaskHeartBeatService taskHeartBeatService);

    /**
     * 注册守护线程任务
     * @param taskDaemonThreadService
     */
    void registerDaemonTask(TaskDaemonThreadService taskDaemonThreadService);

    /**
     * 取消守护线程任务
     * @param taskDaemonThreadService
     */
    void unRegisterDaemonTask(TaskDaemonThreadService taskDaemonThreadService);
}
