package com.qihoo.finance.chronus.core.task.service;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/8/15.
 */
public interface TaskRuntimeService {

    void insert(TaskRuntimeEntity taskRuntimeEntity);

    TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity);

    void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity);

    List<TaskRuntimeEntity> selectTaskRuntimeByTaskName(String cluster, String taskName);

    void delete(TaskRuntimeEntity taskRuntimeEntity);
}
