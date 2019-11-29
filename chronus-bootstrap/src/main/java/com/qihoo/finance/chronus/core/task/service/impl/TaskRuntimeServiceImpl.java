package com.qihoo.finance.chronus.core.task.service.impl;

import com.qihoo.finance.chronus.core.task.service.TaskRuntimeService;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskRuntimeDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xiongpu on 2019/8/15.
 */
@Slf4j
@Service("taskRuntimeService")
public class TaskRuntimeServiceImpl implements TaskRuntimeService {

    @Resource
    private TaskRuntimeDao taskRuntimeDao;

    @Override
    public void insert(TaskRuntimeEntity taskRuntimeEntity) {
        taskRuntimeDao.insert(taskRuntimeEntity);
    }

    @Override
    public TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity) {
        return taskRuntimeDao.taskRuntimeIsExist(taskRuntimeEntity);
    }

    @Override
    public void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity) {
        taskRuntimeDao.updateTaskRuntimeHeartBeatTime(taskRuntimeEntity);
    }

    @Override
    public List<TaskRuntimeEntity> selectTaskRuntimeByTaskName(String cluster, String taskName, Integer judgeDeadInterval) {
        return taskRuntimeDao.selectTaskRuntimeByTaskName(cluster, taskName, judgeDeadInterval);
    }

    @Override
    public void delete(TaskRuntimeEntity taskRuntimeEntity) {
        taskRuntimeDao.delete(taskRuntimeEntity);
    }
}
