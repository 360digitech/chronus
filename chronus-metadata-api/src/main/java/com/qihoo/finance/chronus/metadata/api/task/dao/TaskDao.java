package com.qihoo.finance.chronus.metadata.api.task.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TaskDao {
    void insert(TaskEntity taskEntity);

    void update(TaskEntity taskEntity);

    void delete(String taskName);

    TaskEntity selectById(String id);

    TaskEntity selectTaskInfoByTaskName(String cluster, String taskName);

    List<TaskEntity> selectListAll();

    PageResult<TaskEntity> selectListByPage(Integer page, Integer limit, Map<String, String> param);

    List<TaskEntity> selectTaskInfoByCluster(String cluster);

    List<TaskEntity> selectTaskInfoByTag(String tag);
}
