package com.qihoo.finance.chronus.worker.service.impl;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.worker.service.TaskManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;

/**
 * Created by xiongpu on 2019/11/5.
 */
@Slf4j
public class TaskManagerFactory {

    @Resource
    private NodeInfo currentNode;

    public TaskManager create(TaskEntity taskEntity, TaskItemEntity taskItemEntity) {
        TaskManager taskManager = null;
        try {
            TaskRuntimeEntity taskRuntimeEntity = new TaskRuntimeEntity(taskItemEntity);
            taskManager = new TaskManagerImpl(taskEntity, taskItemEntity, taskRuntimeEntity);
            taskManager.start();

            taskItemEntity.setCluster(currentNode.getCluster());
            taskItemEntity.createdSuccess();
            taskRuntimeEntity.createdSuccess();
            return taskManager;
        } catch (Exception e) {
            log.error("taskName:{},seqNo:{} 启动异常!", taskEntity.getTaskName(), taskItemEntity.getSeqNo(), e);
            taskItemEntity.createdFail("启动异常:" + e.getMessage());
            if (taskManager != null) {
                taskManager.stop();
            }
            return null;
        }
    }


}
