package com.qihoo.finance.chronus.executor.service.impl;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.task.service.TaskRuntimeService;
import com.qihoo.finance.chronus.executor.service.TaskDaemonThreadService;
import com.qihoo.finance.chronus.executor.service.TaskHeartBeatService;
import com.qihoo.finance.chronus.executor.service.TaskManager;
import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Date;

/**
 * Created by xiongpu on 2019/11/5.
 */
@Slf4j
public class TaskManagerFactory {

    @Resource
    private NodeInfo currentNode;
    @Resource
    private TaskRuntimeService taskRuntimeService;
    @Resource
    private TaskHeartBeatService taskHeartBeatService;
    @Resource
    private TaskDaemonThreadService taskDaemonThreadService;

    public TaskManager create(TaskEntity taskEntity, TaskAssignResultEntity taskAssignResultEntity) throws Exception {
        TaskRuntimeEntity taskRuntime = null;
        TaskManager taskManager = null;
        try {
            taskRuntime = createTaskRuntimeInfo(taskEntity, taskAssignResultEntity);
            taskManager = new TaskManagerImpl(taskEntity, taskRuntime);
            taskManager.registerHeartBeatTask(taskHeartBeatService);
            taskManager.registerDaemonTask(taskDaemonThreadService);
            taskRuntime.setMessage("初始化成功!");
        } catch (Exception e) {
            log.error("taskName:{},seqNo:{} 启动异常!", taskEntity.getTaskName(), taskAssignResultEntity.getSeqNo());
            try {
                if (taskManager != null) {
                    taskManager.unRegisterHeartBeatTask(taskHeartBeatService);
                    taskManager.unRegisterDaemonTask(taskDaemonThreadService);
                }
                if (taskRuntime != null) {
                    taskRuntime.setState(ScheduleServerStatusEnum.error.toString());
                    taskRuntime.setMessage("启动异常:" + e.getMessage());
                    taskRuntimeService.updateTaskRuntimeHeartBeatTime(taskRuntime);
                }
            } catch (Exception e1) {
                log.error("更新任务运行信息异常", e1);
            }
            throw e;
        }
        return taskManager;
    }


    private TaskRuntimeEntity createTaskRuntimeInfo(TaskEntity taskEntity, TaskAssignResultEntity taskAssignResultEntity) {
        TaskRuntimeEntity taskRuntime = new TaskRuntimeEntity(taskEntity);
        taskRuntime.setTaskItems(taskAssignResultEntity.getTaskItems());
        taskRuntime.setSeqNo(taskAssignResultEntity.getSeqNo());
        taskRuntime.setAddress(currentNode.getAddress());
        taskRuntime.setHostName(currentNode.getHostName());
        taskRuntime.setHeartBeatTime(new Date());
        TaskRuntimeEntity dbTaskRuntime = taskRuntimeService.taskRuntimeIsExist(taskRuntime);
        if (dbTaskRuntime != null) {
            taskRuntime.setId(dbTaskRuntime.getId());
        } else {
            taskRuntimeService.insert(taskRuntime);
        }
        return taskRuntime;
    }
}
