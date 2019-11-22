package com.qihoo.finance.chronus.support;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.domain.Response;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.core.event.bo.EventBO;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.core.event.service.EventService;
import com.qihoo.finance.chronus.executor.config.ExecutorProperties;
import com.qihoo.finance.chronus.executor.service.ExecutorService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadPhaseEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskCreateStateEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/8/2.
 */
@Slf4j
public class ExecutorSupport implements Support {
    private static final ScheduledExecutorService LOAD_TASK_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_EXECUTOR, SupportConstants.MAIN));
    /**
     * 加载但是创建失败的列表
     */
    private static Table<String, Integer, TaskAssignResultEntity> LOAD_CREATE_ERROR_TABLE = HashBasedTable.create();
    @Resource
    private NodeInfo currentNode;

    @Resource
    private NamingService namingService;

    @Resource
    private ExecutorService executorService;

    @Resource
    private ExecutorProperties executorProperties;

    @Resource
    private EventService eventService;

    private ExecutorTaskStateBO executorTaskStateBO = new ExecutorTaskStateBO();

    @Override
    public void start() throws Exception {
        Node node = namingService.getCurrentNode();
        executorTaskStateBO.init(currentNode.getVersion(), node.getDataVersion());
        LOAD_TASK_SCHEDULE.scheduleWithFixedDelay(executorLoadTaskTimerTask, executorProperties.getLoadTaskDataTimerTaskInitialDelay(), executorProperties.getLoadTaskDataTimerTaskDelay(), TimeUnit.SECONDS);
    }

    private Runnable executorLoadTaskTimerTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_EXECUTOR, "loadTask") {
        @Override
        public void process() throws Exception {
            String masterNodeAddress;
            while (true) {
                masterNodeAddress = namingService.getCurrentMasterNodeAddress();
                if (masterNodeAddress == null) {
                    log.warn("Master 节点心跳已经失效, 跳过本次数据加载!");
                    return;
                }
                Node node = namingService.getCurrentNode();
                if (node == null) {
                    log.warn("当前节点已下线, 停止所有任务加载!");
                    return;
                }

                executorTaskStateBO.setDataVersion(node.getDataVersion());
                if (CollectionUtils.isNotEmpty(LOAD_CREATE_ERROR_TABLE.values())) {
                    EventBO eventBO = EventBO.start(currentNode, EventEnum.EXECUTOR_ADD_ERROR_TASK);
                    List<TaskAssignResultEntity> loadResultList = executorService.addNewTask(new ArrayList<>(LOAD_CREATE_ERROR_TABLE.values()), LOAD_CREATE_ERROR_TABLE);
                    Map<String, List<TaskAssignResultEntity>> resultMap = loadResultList.stream().collect(Collectors.groupingBy(e -> e.getState()));
                    eventBO.stop("补充异常任务结果 成功数:" + resultMap.getOrDefault(TaskCreateStateEnum.SUCC.getState(), new ArrayList<>()).size() + ",失败数:" + resultMap.getOrDefault(TaskCreateStateEnum.FAIL.getState(), new ArrayList<>()).size());
                    eventService.submitEvent(eventBO);
                }
                executorService.submitNodeDataState(masterNodeAddress, executorTaskStateBO);

                EventBO eventBO = EventBO.start(currentNode, EventEnum.EXECUTOR_LOAD_TASK);
                ExecutorTaskStateBO tmpExecutorTaskStateBO = executorService.getNodeData(masterNodeAddress, executorTaskStateBO);
                if (tmpExecutorTaskStateBO != null && (CollectionUtils.isNotEmpty(tmpExecutorTaskStateBO.getNeedStopTaskList()) || CollectionUtils.isNotEmpty(tmpExecutorTaskStateBO.getNeedStartTaskList()))) {
                    executorTaskStateBO = tmpExecutorTaskStateBO;
                    Response response = receiveProcess(node);
                    eventBO.stop("分配结果,阶段:" + tmpExecutorTaskStateBO.getExecutorPhase() + "结果:" + response.getFlag() + " " + response.getMsg());
                    eventService.submitEvent(eventBO);
                }
                log.info("执行机加载数据等待中, {}", tmpExecutorTaskStateBO);
            }
        }
    };

    private Response receiveProcess(Node node) throws UnsupportedEncodingException {
        Response response = new Response().success();
        try {
            if (ExecutorLoadPhaseEnum.isRemovePhase(executorTaskStateBO.getExecutorPhase()) || ExecutorLoadPhaseEnum.isOfflinePhase(executorTaskStateBO.getExecutorPhase())) {
                log.info("收到任务指派结果, 移除任务数:{}", executorTaskStateBO.getNeedStopTaskList().size());
                executorService.removeStopTask(executorTaskStateBO.getNeedStopTaskList(), LOAD_CREATE_ERROR_TABLE);
                response.setMsg("移除任务数:" + executorTaskStateBO.getNeedStopTaskList().size());
            } else if (NodeStateEnum.isNormal(node.getState()) && ExecutorLoadPhaseEnum.isAddPhase(executorTaskStateBO.getExecutorPhase())) {
                log.info("收到任务指派结果, 新增任务数:{}", executorTaskStateBO.getNeedStartTaskList().size());
                List<TaskAssignResultEntity> loadResultList = executorService.addNewTask(executorTaskStateBO.getNeedStartTaskList(), LOAD_CREATE_ERROR_TABLE);
                Map<String, List<TaskAssignResultEntity>> resultMap = loadResultList.stream().collect(Collectors.groupingBy(e -> e.getState()));
                response.setMsg("补充成功数:" + resultMap.getOrDefault(TaskCreateStateEnum.SUCC.getState(), new ArrayList<>()).size() + ",失败数:" + resultMap.getOrDefault(TaskCreateStateEnum.FAIL.getState(), new ArrayList<>()).size());
            }
            executorTaskStateBO.setExecutorState(ExecutorLoadStateEnum.SUCC.getState());
            log.info("加载任务数据完成 nodeDataBO:{}", executorTaskStateBO);
        } catch (Exception e) {
            log.error("任务加载异常 ", e);
            executorTaskStateBO.setExecutorState(ExecutorLoadStateEnum.FAIL.getState());
            response.fail("处理异常" + e.getMessage());
        }
        return response;
    }

    @Override
    public void stop() {
        LOAD_CREATE_ERROR_TABLE.clear();
        LOAD_TASK_SCHEDULE.shutdown();
    }

}
