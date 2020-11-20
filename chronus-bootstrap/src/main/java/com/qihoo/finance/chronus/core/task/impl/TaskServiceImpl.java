package com.qihoo.finance.chronus.core.task.impl;

import com.qihoo.finance.chronus.common.enums.TaskStateEnum;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.task.TaskService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2018/11/5.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Override
    public TaskEntity selectTaskInfoByTaskName(String taskName) {
        return taskDao.selectTaskInfoByTaskName(taskName);
    }

    @Override
    public PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity, List<String> dealSysCodes) {
        return taskDao.selectListByPage(taskEntity, dealSysCodes);
    }

    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        return taskDao.selectTaskInfoByCluster(cluster);
    }

    @Override
    public List<TaskEntity> selectAllTaskInfo() {
        return taskDao.selectAllTaskInfo();
    }

    @Override
    public Map<String, TaskEntity> selectTaskInfoByTaskNames(Collection<String> taskNameSet) {
        List<TaskEntity> taskEntityList = taskDao.selectTaskInfoByTaskNames(taskNameSet);
        Map<String, TaskEntity> taskEntityMap = CollectionUtils.isEmpty(taskEntityList) ? new HashMap<>() : taskEntityList.stream().collect(Collectors.toMap(e -> e.getTaskName(), e -> e));
        return taskEntityMap;
    }

    @Override
    public Set<String> existTaskByTag(String tagName) {
        return taskDao.selectTaskInfoByTag(tagName).stream().map(TaskEntity::getTaskName).collect(Collectors.toSet());
    }

    @Override
    public void update(TaskEntity taskEntity) {
        taskEntity.setDateUpdated(DateUtils.now());
        taskDao.update(taskEntity);
    }

    @Override
    public void insert(TaskEntity taskEntity) {
        taskEntity.setId(null);
        taskEntity.setDateCreated(DateUtils.now());
        taskEntity.setDateUpdated(DateUtils.now());
        taskDao.insert(taskEntity);
    }

    @Override
    public void delete(String taskName) {
        taskDao.delete(taskName);
    }

    @Override
    public TaskEntity selectById(String id) {
        return taskDao.selectById(id);
    }

    @Override
    public void start(String taskName) {
        TaskEntity taskEntity = selectTaskInfoByTaskName(taskName);
        if (taskEntity != null) {
            taskEntity.setUpdatedBy(taskEntity.getUpdatedBy());
            taskEntity.setState(TaskStateEnum.START.name());
            taskDao.update(taskEntity);
        }
    }

    @Override
    public void stop(String taskName) {
        TaskEntity taskEntity = selectTaskInfoByTaskName(taskName);
        if (taskEntity != null) {
            taskEntity.setUpdatedBy(taskEntity.getUpdatedBy());
            taskEntity.setState(TaskStateEnum.STOP.name());
            taskDao.update(taskEntity);
        }
    }


    @Override
    public List<TaskEntity> selectTaskInfoBySysCode(String dealSysCode) {
        return taskDao.selectTaskInfoBySysCode(dealSysCode);
    }
}
