package com.qihoo.finance.chronus.worker.service;

import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by xiongpu on 2019/9/9.
 */
public interface WorkerService {

    TaskRuntimeEntity getTaskRuntimeInfo(String taskName, String taskItemSeqNo);

    /**
     * 移除并且暂停任务
     */
    void destroyAllTask();

    void destroyTask(TaskItemEntity taskItemEntity);

    TaskItemEntity createTask(TaskItemEntity taskItemEntity, TaskEntity taskEntity);

    /**
     * 调用master 获取分配结果
     *
     * @param masterNodeAddress
     * @param address
     * @param dataVersion
     * @return
     * @throws UnsupportedEncodingException
     */
    WorkerTaskStateBO getNodeData(String masterNodeAddress, String address, String dataVersion) throws UnsupportedEncodingException;

    /**
     * 提交加载状态到master
     *
     * @param masterNodeAddress
     * @param workerTaskStateBO
     * @throws UnsupportedEncodingException
     */
    Response submitNodeDataState(String masterNodeAddress, WorkerTaskStateBO workerTaskStateBO);

    /**
     * 获取所有的task信息
     *
     * @return
     */
    List<TaskItemEntity> getTaskRuntimeStateOfError();
}
