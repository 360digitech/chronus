package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

import java.util.List;
import java.util.Map;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class TaskH2DaoImpl implements TaskDao {

    @Override
    public void insert(TaskEntity taskEntity) {

    }

    @Override
    public void update(TaskEntity taskEntity) {

    }

    @Override
    public void delete(String taskName) {

    }

    @Override
    public TaskEntity selectById(String id) {
        return null;
    }

    @Override
    public TaskEntity selectTaskInfoByTaskName(String cluster, String taskName) {
        return null;
    }

    @Override
    public List<TaskEntity> selectListAll() {
        return null;
    }

    @Override
    public PageResult<TaskEntity> selectListByPage(Integer page, Integer limit, Map<String, String> param) {
        return null;
    }

    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        return null;
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTag(String tag) {
        return null;
    }
}
