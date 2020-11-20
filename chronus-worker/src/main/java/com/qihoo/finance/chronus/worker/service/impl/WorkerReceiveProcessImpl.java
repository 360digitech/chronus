package com.qihoo.finance.chronus.worker.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.core.log.NodeLogBO;
import com.qihoo.finance.chronus.core.log.NodeLogService;
import com.qihoo.finance.chronus.core.log.bo.NodeLogLoadTask;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.core.task.TaskService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskItemStateEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.WorkerLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.worker.service.WorkerReceiveProcess;
import com.qihoo.finance.chronus.worker.service.WorkerService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class WorkerReceiveProcessImpl implements WorkerReceiveProcess {
    private Node node;
    private WorkerTaskStateBO workerTaskStateBO;
    private WorkerTaskStateBO newWorkerTaskStateBO;
    private String masterNodeAddress;
    private boolean interruptProcessFlag = false;
    private NodeLogBO nodeLogBO;
    private Response receiveResponse;
    @Resource
    private NodeInfo currentNode;
    @Resource
    private WorkerService workerService;
    @Resource
    private TaskService taskService;
    @Resource
    private NodeLogService nodeLogService;

    @Override
    public WorkerReceiveProcess init(Node node, WorkerTaskStateBO workerTaskStateBO, String masterNodeAddress) {
        this.node = node;
        this.workerTaskStateBO = workerTaskStateBO;
        this.masterNodeAddress = masterNodeAddress;
        this.interruptProcessFlag = false;
        return this;
    }

    @Override
    public WorkerReceiveProcess submitState() {
        if (MapUtils.isEmpty(workerTaskStateBO.getAssignChangeMap())) {
            workerTaskStateBO.setDataVersion(node.getDataVersion());
        }
        // 如果当前节点状态为RESET 不上报状态
        if(WorkerLoadStateEnum.RESET.name().equals(workerTaskStateBO.getWorkerState())){
            return this;
        }
        Response submitStateResponse = workerService.submitNodeDataState(masterNodeAddress, workerTaskStateBO);
        if (submitStateResponse.failed()) {
            interrupt();
            log.error("上报节点状态结果异常:{}", submitStateResponse.getMsg());
        } else {
            workerTaskStateBO.setWorkerState(WorkerLoadStateEnum.READY.name());
            workerTaskStateBO.setAssignChangeMap(new HashMap<>());
        }
        return this;
    }

    @Override
    public WorkerReceiveProcess getAssignResult() {
        if (interruptProcessFlag) {
            return this;
        }
        try {
            this.newWorkerTaskStateBO = workerService.getNodeData(masterNodeAddress, node.getAddress(), node.getDataVersion());
            if (newWorkerTaskStateBO == null || !WorkerLoadStateEnum.RESET.name().equals(newWorkerTaskStateBO.getWorkerState()) || MapUtils.isEmpty(newWorkerTaskStateBO.getAssignChangeMap())) {
                interrupt();
                return this;
            }
            workerTaskStateBO.setWorkerState(WorkerLoadStateEnum.RESET.name());
            this.nodeLogBO = NodeLogBO.start(currentNode, NodeLogTypeEnum.WORKER_LOAD_TASK);
        } catch (Exception e) {
            log.error("获取任务分配结果异常,masterNodeAddress:{}", masterNodeAddress, e);
            interrupt();
        }
        return this;
    }

    @Override
    public WorkerReceiveProcess receiveProcess() {
        if (interruptProcessFlag) {
            return this;
        }
        receiveResponse = new Response().success();
        List<TaskItemEntity> loadResultList = new ArrayList<>(newWorkerTaskStateBO.getAssignChangeMap().values());
        Map<String, TaskEntity> taskEntityMap = taskService.selectTaskInfoByTaskNames(loadResultList.stream().map(e -> e.getTaskName()).collect(Collectors.toSet()));

        List<NodeLogLoadTask> nodeLogLoadTaskList = new ArrayList<>();
        for (TaskItemEntity taskItemEntity : loadResultList) {
            NodeLogLoadTask nodeLogLoadTask = new NodeLogLoadTask(taskItemEntity);

            if (TaskItemStateEnum.isWaitStop(taskItemEntity.getState())) {
                workerService.destroyTask(taskItemEntity);
                taskItemEntity.setState(TaskItemStateEnum.STOP.name());
                workerTaskStateBO.getTaskAssignResultMap().remove(taskItemEntity.getTaskItemId());
            }
            if ((TaskItemStateEnum.isWaitStart(taskItemEntity.getState()) && Objects.equals(node.getAddress(), taskItemEntity.getWorkerAddress()))) {
                TaskEntity taskEntity = taskEntityMap.get(taskItemEntity.getTaskName());
                workerService.createTask(taskItemEntity, taskEntity);
                workerTaskStateBO.getTaskAssignResultMap().put(taskItemEntity.getTaskItemId(), taskItemEntity);
            }
            workerTaskStateBO.getAssignChangeMap().put(taskItemEntity.getTaskItemId(), taskItemEntity);

            nodeLogLoadTask.finish(taskItemEntity.getState(), taskItemEntity.getMessage());
            nodeLogLoadTaskList.add(nodeLogLoadTask);
        }
        receiveResponse.setData(nodeLogLoadTaskList);
        receiveResponse.setMsg("变更:" + loadResultList.size());
        workerTaskStateBO.setWorkerState(WorkerLoadStateEnum.FINISH.name());
        log.info("加载任务数据完成 workerTaskStateBO:{}", workerTaskStateBO);
        return this;
    }

    @Override
    public WorkerReceiveProcess finish() {
        if (interruptProcessFlag) {
            return this;
        }
        nodeLogBO.stop("分配结果,结果:" + receiveResponse.getFlag() + " " + receiveResponse.getMsg(), JSONObject.toJSONString(receiveResponse.getData()));
        nodeLogService.submitEvent(nodeLogBO);
        return this;
    }

    @Override
    public WorkerTaskStateBO getWorkerTaskStateBO() {
        if (interruptProcessFlag) {
            return null;
        }
        return workerTaskStateBO;
    }

    private void interrupt() {
        this.interruptProcessFlag = true;
    }
}
