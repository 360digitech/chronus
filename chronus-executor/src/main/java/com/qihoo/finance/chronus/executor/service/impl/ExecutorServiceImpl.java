package com.qihoo.finance.chronus.executor.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.executor.service.ExecutorService;
import com.qihoo.finance.chronus.executor.service.TaskManager;
import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskCreateStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

/**
 * Created by xiongpu on 2019/9/9.
 */
@Slf4j
public class ExecutorServiceImpl implements ExecutorService {
    private static Table<String, Integer, TaskManager> taskManagerTable = HashBasedTable.create();

    @Resource
    private TaskService taskService;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private NodeInfo currentNode;

    @Resource
    private TaskManagerFactory taskManagerFactory;

    @Override
    public void removeStopTask(List<TaskAssignResultEntity> taskAssignResultEntityList, Table<String, Integer, TaskAssignResultEntity> loadCreateErrorTable) {
        if (CollectionUtils.isEmpty(taskAssignResultEntityList)) {
            log.info("当前机器已经下线 暂停所有的任务! ");
            stopAllTaskManager();
            loadCreateErrorTable.clear();
            return;
        }
        for (TaskAssignResultEntity taskAssignResultEntity : taskAssignResultEntityList) {
            loadCreateErrorTable.remove(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo());
            TaskManager taskManager = taskManagerTable.remove(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo());
            if (taskManager != null) {
                taskManager.stop();
                log.info("TaskName:{}, seqNo:{}, taskItems:{} 已停止!", taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo(), taskAssignResultEntity.getTaskItems());
            }
        }
    }

    @Override
    public List<TaskAssignResultEntity> addNewTask(List<TaskAssignResultEntity> taskAssignResultEntityList, Table<String, Integer, TaskAssignResultEntity> loadCreateErrorTable) {
        for (TaskAssignResultEntity taskAssignResultEntity : taskAssignResultEntityList) {
            if (taskAssignResultEntity.getAssignNum() <= 0) {
                continue;
            }
            TaskManager taskManager = taskManagerTable.get(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo());
            if (taskManager == null) {
                TaskEntity taskEntity = taskService.selectTaskInfoByTaskName(currentNode.getCluster(), taskAssignResultEntity.getTaskName());
                if (taskEntity == null) {
                    continue;
                }
                try {
                    taskManager = taskManagerFactory.create(taskEntity, taskAssignResultEntity);
                    taskManagerTable.put(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo(), taskManager);
                    taskAssignResultEntity.setState(TaskCreateStateEnum.SUCC.getState());
                    log.info("TaskName:{}, seqNo:{}, taskItems:{} 已创建!", taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo(), taskAssignResultEntity.getTaskItems());
                } catch (Exception e) {
                    log.error("创建任务管理器异常,currentNode:{},taskAssignResultEntity:{}", currentNode, taskAssignResultEntity, e);
                    taskAssignResultEntity.setState(TaskCreateStateEnum.FAIL.getState());
                    loadCreateErrorTable.put(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo(), taskAssignResultEntity);
                }
            } else {
                log.error("创建任务管理器异常,当前id已存在!,taskAssignResultEntity:{}", taskAssignResultEntity);
            }
        }
        return taskAssignResultEntityList;
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
    public ExecutorTaskStateBO getNodeData(String masterNodeAddress, ExecutorTaskStateBO executorTaskStateBO) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ChronusConstants.API_TOKEN_KEY, SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY));

        HttpEntity<ExecutorTaskStateBO> requestEntity = new HttpEntity<>(executorTaskStateBO, headers);
        String url = "http://" + masterNodeAddress + "/api/master/pull/" + URLEncoder.encode(currentNode.getAddress(), "UTF-8");
        log.info("invoke url:{} get assign result!  executorTaskStateBO:{}", url, executorTaskStateBO);
        ResponseEntity<ExecutorTaskStateBO> responseEntity = restTemplate.exchange(url, HttpMethod.POST, requestEntity, ExecutorTaskStateBO.class);
        if (HttpStatus.OK.equals(responseEntity.getStatusCode())) {
            log.info("invoke url:{} get assign result:{}", url, responseEntity.getBody());
            return responseEntity.getBody();
        }
        return null;
    }

    @Override
    public void submitNodeDataState(String masterNodeAddress, ExecutorTaskStateBO executorTaskStateBO) throws UnsupportedEncodingException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(ChronusConstants.API_TOKEN_KEY, SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY));
        HttpEntity<ExecutorTaskStateBO> requestEntity = new HttpEntity<>(executorTaskStateBO, headers);
        restTemplate.exchange("http://" + masterNodeAddress + "/api/master/push/" + URLEncoder.encode(currentNode.getAddress(), "UTF-8"), HttpMethod.POST, requestEntity, ExecutorTaskStateBO.class);
    }
}
