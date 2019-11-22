package com.qihoo.finance.chronus.core.task.service;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

import java.util.List;
import java.util.Set;

/**
 * Created by xiongpu on 2018/11/5.
 */
public interface TaskService {

    TaskEntity selectTaskInfoByTaskName(String cluster, String taskName);

    List<TaskEntity> selectListAll();

    PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity);

    List<TaskEntity> selectTaskInfoByCluster(String cluster);

    Set<String> existTaskByTag(String tagName);

    void update(TaskEntity taskEntity);

    void insert(TaskEntity taskEntity);

    void delete(String taskName);

    TaskEntity selectById(TaskEntity taskEntity);

}
