package com.qihoo.finance.chronus.core.task;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiongpu on 2018/11/5.
 */
public interface TaskService {

    TaskEntity selectTaskInfoByTaskName(String taskName);

    PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity, List<String> dealSysCodes);

    List<TaskEntity> selectTaskInfoByCluster(String cluster);

    /**
     * 获取所有任务,不区分集群
     *
     * @return
     */
    List<TaskEntity> selectAllTaskInfo();

    Map<String, TaskEntity> selectTaskInfoByTaskNames(Collection<String> taskNameSet);

    Set<String> existTaskByTag(String tagName);

    void update(TaskEntity taskEntity);

    void insert(TaskEntity taskEntity);

    void delete(String taskName);

    TaskEntity selectById(String id);

    void start(String taskName);

    void stop(String taskName);

    List<TaskEntity> selectTaskInfoBySysCode(String dealSysCode);
}
