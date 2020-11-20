package com.qihoo.finance.chronus.metadata.api.task.dao;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TaskItemDao {

    TaskItemEntity getTaskItem(String taskItemId);

    List<TaskItemEntity> getTaskItemList(String taskName);

    void create(TaskItemEntity taskItemEntity);

    void delete(TaskItemEntity taskItemEntity);

    void deleteByTaskItemInfo(TaskItemEntity taskItemEntity);

    List<TaskItemEntity> selectAllTaskItem();

    List<TaskItemEntity> selectTaskItemByCluster(String cluster);

    void update(TaskItemEntity taskItemEntity);

    boolean restartLock(TaskItemEntity taskItemEntity);
}
