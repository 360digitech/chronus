package com.qihoo.finance.chronus.core.task.impl;

import com.qihoo.finance.chronus.core.task.TaskItemService;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskItemDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service("taskItemService")
public class TaskItemServiceImpl implements TaskItemService {

    @Resource
    private TaskItemDao taskItemDao;

    @Override
    public TaskItemEntity getTaskItem(String taskItemId) {
        return taskItemDao.getTaskItem(taskItemId);
    }

    @Override
    public List<TaskItemEntity> getTaskItemList(String taskName) {
        return taskItemDao.getTaskItemList(taskName);
    }

    @Override
    public void create(TaskItemEntity taskItemEntity) {
        taskItemDao.create(taskItemEntity);
    }

    @Override
    public void delete(TaskItemEntity taskItemEntity) {
        taskItemDao.delete(taskItemEntity);
    }

    @Override
    public void deleteByTaskItemInfo(TaskItemEntity taskItemEntity) {
        taskItemDao.deleteByTaskItemInfo(taskItemEntity);
    }

    @Override
    public List<TaskItemEntity> selectAllTaskItem() {
        return taskItemDao.selectAllTaskItem();
    }

    @Override
    public List<TaskItemEntity> selectTaskItemByCluster(String cluster) {
        return taskItemDao.selectTaskItemByCluster(cluster);
    }

    @Override
    public void update(TaskItemEntity taskItemEntity) {
        taskItemDao.update(taskItemEntity);
    }

    @Override
    public boolean restartLock(TaskItemEntity taskItemEntity) {
        return taskItemDao.restartLock(taskItemEntity);
    }
}
