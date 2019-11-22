package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.task.dao.TaskRuntimeDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.List;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class TaskRuntimeH2DaoImpl implements TaskRuntimeDao {

    @Override
    public void batchInsert(List<TaskRuntimeEntity> taskRuntimeEntityInitList) {

    }

    @Override
    public TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity) {
        return null;
    }

    @Override
    public void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity) {

    }

    @Override
    public TaskRuntimeEntity selectTaskRuntimeByTaskName(String cluster, String taskName) {
        return null;
    }
}
