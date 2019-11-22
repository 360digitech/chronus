package com.qihoo.finance.chronus.metadata.api.task.dao;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TaskRuntimeDao {
    void insert(TaskRuntimeEntity taskRuntimeEntity);

    void batchInsert(List<TaskRuntimeEntity> taskRuntimeEntityInitList);

    TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity);

    void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity);

    List<TaskRuntimeEntity> selectTaskRuntimeByTaskName(String cluster, String taskName);

    void delete(TaskRuntimeEntity taskRuntimeEntity);
}
