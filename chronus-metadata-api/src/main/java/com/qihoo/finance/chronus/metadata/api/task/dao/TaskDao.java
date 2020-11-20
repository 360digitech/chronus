package com.qihoo.finance.chronus.metadata.api.task.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

import java.util.Collection;
import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TaskDao {
    void insert(TaskEntity taskEntity);

    void update(TaskEntity taskEntity);

    void delete(String taskName);

    TaskEntity selectById(String id);

    TaskEntity selectTaskInfoByTaskName(String taskName);

    PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity, List<String> dealSysCodes);

    List<TaskEntity> selectTaskInfoByCluster(String cluster);

    List<TaskEntity> selectAllTaskInfo();

    List<TaskEntity> selectTaskInfoByTaskNames(Collection<String> taskNameSet);

    List<TaskEntity> selectTaskInfoByTag(String tag);

    List<TaskEntity> selectTaskInfoBySysCode(String dealSysCode);
}
