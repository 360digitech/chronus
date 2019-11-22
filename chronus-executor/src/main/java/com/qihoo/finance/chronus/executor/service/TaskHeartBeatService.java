package com.qihoo.finance.chronus.executor.service;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

/**
 * Created by xiongpu on 2019/8/26.
 */
public interface TaskHeartBeatService {
    /**
     *
     * @param heartBeatRate
     * @param taskRuntime
     */
    void addTaskToHeartBeatQueue(Integer heartBeatRate, TaskRuntimeEntity taskRuntime);

    /**
     * 从队列移除掉这个任务
     *
     * @param taskRuntime
     */
    void removeTaskFromHeartBeatQueue(Integer heartBeatRate, TaskRuntimeEntity taskRuntime);
}
