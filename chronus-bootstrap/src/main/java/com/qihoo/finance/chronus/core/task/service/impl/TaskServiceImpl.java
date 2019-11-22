package com.qihoo.finance.chronus.core.task.service.impl;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2018/11/5.
 */
@Service("taskService")
public class TaskServiceImpl implements TaskService {

    @Resource
    private TaskDao taskDao;

    @Override
    public TaskEntity selectTaskInfoByTaskName(String cluster, String taskName) {
        return taskDao.selectTaskInfoByTaskName(cluster, taskName);
    }

    @Override
    public List<TaskEntity> selectListAll() {
        return taskDao.selectListAll();
    }

    @Override
    public PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity) {
        Map<String, String> param = new HashMap<>();
        if (StringUtils.isNotBlank(taskEntity.getDealSysCode())) {
            param.put("dealSysCode", taskEntity.getDealSysCode());
        }
        if (StringUtils.isNotBlank(taskEntity.getCluster())) {
            param.put("cluster", taskEntity.getCluster());
        }
        if (StringUtils.isNotBlank(taskEntity.getTaskName())) {
            param.put("taskName", taskEntity.getTaskName());
        }
        if (StringUtils.isNotBlank(taskEntity.getState())) {
            param.put("state", taskEntity.getState());
        }
        return taskDao.selectListByPage(taskEntity.getPageNum(), taskEntity.getPageSize(), param);
    }

    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        return taskDao.selectTaskInfoByCluster(cluster);
    }

    @Override
    public Set<String> existTaskByTag(String tagName) {
        return taskDao.selectTaskInfoByTag(tagName).stream().map(TaskEntity::getTaskName).collect(Collectors.toSet());
    }


    @Override
    public void update(TaskEntity taskEntity) {
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
    public TaskEntity selectById(TaskEntity taskEntity) {
        return taskDao.selectById(taskEntity.getId());
    }
}
