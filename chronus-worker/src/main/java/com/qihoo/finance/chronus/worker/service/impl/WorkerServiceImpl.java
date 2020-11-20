package com.qihoo.finance.chronus.worker.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.task.TaskItemService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskItemStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.worker.service.TaskManager;
import com.qihoo.finance.chronus.worker.service.WorkerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/9/9.
 */
@Slf4j
public class WorkerServiceImpl implements WorkerService {
    /**
     * 任务，任务序号，TaskManager映射表
     */
    private static Table<String, String, TaskManager> taskManagerTable = HashBasedTable.create();

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private NodeInfo currentNode;

    @Resource
    private TaskManagerFactory taskManagerFactory;

    @Resource
    private TaskItemService taskItemService;

    /**
     * 是否有Error状态的task，用于告警
     *
     * @return
     */
    @Override
    public List<TaskItemEntity> getTaskRuntimeStateOfError() {
        List<TaskItemEntity> all = taskItemService.selectAllTaskItem();
        List<TaskItemEntity> result = Lists.newArrayList();
        for (TaskItemEntity t : all) {
            if (TaskItemStateEnum.ERROR.isError(t.getState())) {
                result.add(t);
            }
        }
        return result;
    }

    @Override
    public TaskRuntimeEntity getTaskRuntimeInfo(String taskName, String taskItemSeqNo) {
        TaskManager taskManager = taskManagerTable.get(taskName, taskItemSeqNo);
        if (taskManager != null) {
            return taskManager.getTaskRuntimeInfo();
        }
        return null;
    }

    @Override
    public void destroyAllTask() {
        log.info("当前机器已经下线 暂停所有的任务! ");
        stopAllTaskManager();
    }

    @Override
    public void destroyTask(TaskItemEntity taskItemEntity) {
        TaskManager taskManager = taskManagerTable.remove(taskItemEntity.getTaskName(), String.valueOf(taskItemEntity.getSeqNo()));
        if (taskManager != null) {
            taskManager.stop();
        }
    }

    @Override
    public TaskItemEntity createTask(TaskItemEntity taskItemEntity, TaskEntity taskEntity) {
        TaskManager taskManager = taskManagerTable.get(taskItemEntity.getTaskName(), String.valueOf(taskItemEntity.getSeqNo()));
        if (taskManager != null) {
            log.error("创建任务管理器异常,当前id已存在!,taskItemEntity:{}", taskItemEntity);
            taskItemEntity.createdFail("任务管理器已存在!");
            return taskItemEntity;
        }
        if (taskEntity == null) {
            log.warn("未获取到任务信息，取消本次创建任务运行信息! taskItemEntity:{}", taskItemEntity);
            taskItemEntity.createdFail("未获取到任务信息!");
            return taskItemEntity;
        }
        // 任务在加载过程中发生变更需要等待重新分配 本次加载取消
        if (!DateUtils.toDateTimeText(taskEntity.getDateUpdated()).equals(DateUtils.toDateTimeText(taskItemEntity.getTaskDateUpdated()))) {
            log.warn("获取到任务信息，检测到任务发生了变更，跳过本次任务管理器创建! taskEntity:{} taskItemEntity:{}", taskEntity, taskItemEntity);
            taskItemEntity.createdFail("任务发生了变更!");
            return taskItemEntity;
        }

        TaskItemEntity dbTaskItem = taskItemService.getTaskItem(taskItemEntity.getTaskItemId());
        if (!Objects.equals(dbTaskItem.getVersion(), taskItemEntity.getVersion()) || !Objects.equals(dbTaskItem.getWorkerAddress(), currentNode.getAddress())) {
            log.warn("获取到任务信息，检测到任务发生了变更，跳过本次任务管理器创建! taskEntity:{} taskItemEntity:{} dbTaskItem:{}", taskEntity, taskItemEntity, dbTaskItem);
            taskItemEntity.createdFail("获取任务创建锁失败!");
            return taskItemEntity;
        }

        taskManager = taskManagerFactory.create(taskEntity, taskItemEntity);
        if (taskManager != null) {
            taskManagerTable.put(taskItemEntity.getTaskName(), String.valueOf(taskItemEntity.getSeqNo()), taskManager);
            log.info("TaskName:{}, seqNo:{}, taskItems:{} 已创建!", taskItemEntity.getTaskName(), taskItemEntity.getSeqNo(), taskItemEntity.getTaskItems());
        }
        return taskItemEntity;
    }

    private int stopAllTaskManager() {
        int stopTaskCount = 0;
        if (taskManagerTable.isEmpty()) {
            return stopTaskCount;
        }
        Iterator<TaskManager> iterator = taskManagerTable.values().iterator();
        while (iterator.hasNext()) {
            TaskManager taskManager = iterator.next();
            taskManager.stop();
            iterator.remove();
            stopTaskCount++;
        }
        return stopTaskCount;
    }

    @Override
    public WorkerTaskStateBO getNodeData(String masterNodeAddress, String address, String dataVersion) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ChronusConstants.API_TOKEN_KEY, SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY));

        HttpEntity<WorkerTaskStateBO> requestEntity = new HttpEntity<>(headers);
        String url = "http://" + masterNodeAddress + "/api/master/pull/" + URLEncoder.encode(address, "UTF-8") + "/" + dataVersion;
        log.info("invoke url:{} get assign result!", url);
        ResponseEntity<WorkerTaskStateBO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, WorkerTaskStateBO.class);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            log.info("invoke url:{} get assign result:{}", url, responseEntity.getBody());
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public Response submitNodeDataState(String masterNodeAddress, WorkerTaskStateBO workerTaskStateBO) {
        HttpHeaders headers = new HttpHeaders();
        ResponseEntity<Response> responseEntity;
        try {
            headers.add(ChronusConstants.API_TOKEN_KEY, SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY));
            HttpEntity<WorkerTaskStateBO> requestEntity = new HttpEntity<>(workerTaskStateBO, headers);
            String url = "http://" + masterNodeAddress + "/api/master/push/" + URLEncoder.encode(currentNode.getAddress(), "UTF-8") + "/" + workerTaskStateBO.getDataVersion();
            responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, Response.class);
            if (HttpStatus.OK.equals(responseEntity.getStatusCode()) && responseEntity.getBody() != null) {
                return responseEntity.getBody();
            }
        } catch (Exception e) {
            return new Response().hinderFail("上报失败:" + e.getMessage());
        }
        return new Response().hinderFail("上报失败:" + responseEntity.getStatusCode());
    }
}
