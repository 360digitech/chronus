package com.qihoo.finance.chronus.master.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.core.event.annotation.Event;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.master.bo.DeferredResultWrapper;
import com.qihoo.finance.chronus.master.service.TagAssignService;
import com.qihoo.finance.chronus.master.service.TaskAssignService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadPhaseEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskCreateStateEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/16.
 */
@Slf4j
public class TaskAssignServiceImpl implements TaskAssignService {
    private final List<DeferredResultWrapper<ExecutorTaskStateBO>> deferredResults = new ArrayList<>();
    private volatile static Table<String, String, ExecutorTaskStateBO> executorTaskStateTable = HashBasedTable.create();
    private volatile static Table<String, String, ExecutorTaskStateBO> phase1Map = HashBasedTable.create();
    private volatile static Table<String, String, ExecutorTaskStateBO> phase2Map = HashBasedTable.create();

    @Resource
    private TaskService taskService;

    @Resource
    private NodeInfo currentNode;

    @Resource
    private NamingService namingService;

    @Resource
    private TagAssignService tagAssignService;

    @Override
    public void clear() {
        log.warn("清空本机executorTaskStateMap!");
        phase2Map.clear();
        phase1Map.clear();
        executorTaskStateTable.clear();
    }

    @Override
    public DeferredResult<ResponseEntity<ExecutorTaskStateBO>> pullAssignResult(String address, ExecutorTaskStateBO executorTaskStateBO) {
        log.info("接收到 address:{} 获取分配结果请求! {}", address, executorTaskStateBO);
        DeferredResultWrapper<ExecutorTaskStateBO> deferredResultWrapper = new DeferredResultWrapper(address);
        deferredResultWrapper.onTimeout(() -> log.debug("pullAssignResult timeout {}", address));
        deferredResultWrapper.onCompletion(() -> deferredResults.remove(deferredResultWrapper));
        this.deferredResults.add(deferredResultWrapper);

        ExecutorTaskStateBO cacheExecutorTaskStateBO = executorTaskStateTable.get(address, executorTaskStateBO.getDataVersion());
        if (cacheExecutorTaskStateBO == null) {
            return deferredResultWrapper.getResult();
        }

        if (phase1Map.get(address, executorTaskStateBO.getDataVersion()) != null) {
            ExecutorTaskStateBO result = phase1Map.get(address, executorTaskStateBO.getDataVersion());
            cacheExecutorTaskStateBO.setExecutorPhase(result.getExecutorPhase());
            deferredResultWrapper.setResult(result);
            return deferredResultWrapper.getResult();
        }
        if (phase1Map.isEmpty() && phase2Map.get(address, executorTaskStateBO.getDataVersion()) != null) {
            ExecutorTaskStateBO result = phase2Map.get(address, executorTaskStateBO.getDataVersion());
            cacheExecutorTaskStateBO.setExecutorPhase(result.getExecutorPhase());
            deferredResultWrapper.setResult(result);
            return deferredResultWrapper.getResult();
        }
        return deferredResultWrapper.getResult();
    }

    @Override
    public void submitExecutorState(String address, ExecutorTaskStateBO executorTaskStateBO) throws InterruptedException {
        ExecutorTaskStateBO cacheExecutorTaskStateBO = executorTaskStateTable.get(address, executorTaskStateBO.getDataVersion());
        log.info("接收到 address:{} 上报加载状态请求! {}  cacheExecutorTaskStateBO:{}", address, executorTaskStateBO, cacheExecutorTaskStateBO);
        if (cacheExecutorTaskStateBO == null) {
            executorTaskStateTable.put(address, executorTaskStateBO.getDataVersion(), executorTaskStateBO);
            return;
        }

        if (executorTaskStateBO.offlineSuccess()) {
            cacheExecutorTaskStateBO.setTaskAssignResultList(Lists.newArrayList());
        }

        if (!ExecutorLoadPhaseEnum.isResetPhase(cacheExecutorTaskStateBO.getExecutorPhase())) {
            cacheExecutorTaskStateBO.setExecutorState(executorTaskStateBO.getExecutorState());
            if (executorTaskStateBO.removeSuccess() || executorTaskStateBO.offlineSuccess()) {
                cacheExecutorTaskStateBO.setExecutorPhase(executorTaskStateBO.getExecutorPhase());
                phase1Map.remove(address, executorTaskStateBO.getDataVersion());
                if (phase2Map.get(address, executorTaskStateBO.getDataVersion()) == null) {
                    cacheExecutorTaskStateBO.setExecutorPhase(ExecutorLoadPhaseEnum.FINISH.getPhase());
                }
            } else if (executorTaskStateBO.addSuccess()) {
                cacheExecutorTaskStateBO.setExecutorPhase(executorTaskStateBO.getExecutorPhase());
                phase2Map.remove(address, executorTaskStateBO.getDataVersion());
                cacheExecutorTaskStateBO.setExecutorPhase(ExecutorLoadPhaseEnum.FINISH.getPhase());
            }
        }
    }

    @Override
    @Event(value = EventEnum.MASTER_TASK_ASSIGN)
    public void taskAssign() throws InterruptedException {
        try {
            List<Node> executorNodeList = namingService.getAllExecutorNode();
            if (CollectionUtils.isEmpty(executorNodeList)) {
                log.error("任务所属的集群:{},不存在执行机,请检查! ", currentNode.getCluster());
                return;
            }

            for (Node node : executorNodeList) {
                String address = node.getAddress();
                if (!NodeStateEnum.isNormal(node.getState())) {
                    continue;
                }
                ExecutorTaskStateBO executorTaskStateBO = executorTaskStateTable.get(address, node.getDataVersion());
                if (executorTaskStateBO == null) {
                    log.info("存在机器还未上报加载状态, 跳过本次任务分配! address:{}", address);
                    return;
                } else if (ExecutorLoadPhaseEnum.isRemovePhase(executorTaskStateBO.getExecutorPhase()) || ExecutorLoadPhaseEnum.isAddPhase(executorTaskStateBO.getExecutorPhase()) || ExecutorLoadPhaseEnum.isResetPhase(executorTaskStateBO.getExecutorPhase())) {
                    log.info("存在节点 address:{} phase:{},任务还在加载中, 跳过本次任务分配!", address, executorTaskStateBO.getExecutorPhase());
                    return;
                }
            }

            List<TaskEntity> taskList = taskService.selectTaskInfoByCluster(currentNode.getCluster());
            if (CollectionUtils.isEmpty(taskList)) {
                log.info("该集群:{},不存在调度任务 跳过处理!", currentNode.getCluster());
                return;
            }
            Set<String> allTagNames = taskList.stream().map(TaskEntity::getTag).collect(Collectors.toSet());
            List<Node> executorNormalNodeList = executorNodeList.stream().filter(e -> NodeStateEnum.isNormal(e.getState())).collect(Collectors.toList());
            Map<String, List<Node>> tagExecutorMapping = tagAssignService.tagAssign(executorNormalNodeList, allTagNames);

            log.info("cluster:{} 开始进行任务分配!", currentNode.getCluster());
            // 按tag分组Task
            Map<String, List<TaskEntity>> tagTaskMapping = getTagTaskListMap(taskList);

            for (Map.Entry<String, List<TaskEntity>> entry : tagTaskMapping.entrySet()) {
                String tag = entry.getKey();
                List<Node> nodeList = tagExecutorMapping.get(tag);
                ergodicAssign(tag, entry.getValue(), nodeList);
            }
            phase2Map.clear();
            phase1Map.clear();
            for (Node node : executorNodeList) {
                ExecutorTaskStateBO executorTaskStateBO = executorTaskStateTable.get(node.getAddress(), node.getDataVersion());
                if (executorTaskStateBO == null) {
                    continue;
                }
                if (NodeStateEnum.isOffline(node.getState()) && CollectionUtils.isNotEmpty(executorTaskStateBO.getTaskAssignResultList())) {
                    ExecutorTaskStateBO tmpExecutorTaskStateBO = BeanUtils.copyBean(ExecutorTaskStateBO.class, executorTaskStateBO);
                    tmpExecutorTaskStateBO.offline(executorTaskStateBO.getTaskAssignResultList());
                    phase1Map.put(node.getAddress(), node.getDataVersion(), tmpExecutorTaskStateBO);
                    DeferredResultWrapper<ExecutorTaskStateBO> deferredResultWrapper = deferredResults.stream().filter(e -> e.getInstanceId().equals(node.getAddress())).findFirst().orElse(null);
                    if (deferredResultWrapper != null) {
                        deferredResultWrapper.setResult(phase1Map.get(deferredResultWrapper.getInstanceId(), node.getDataVersion()));
                    }
                    continue;
                }

                if (!ExecutorLoadStateEnum.RESET.getState().equals(executorTaskStateBO.getExecutorState())) {
                    continue;
                }
                if (CollectionUtils.isNotEmpty(executorTaskStateBO.getNeedStopTaskList())) {
                    ExecutorTaskStateBO tmpExecutorTaskStateBO = BeanUtils.copyBean(ExecutorTaskStateBO.class, executorTaskStateBO);
                    tmpExecutorTaskStateBO.setExecutorPhase(ExecutorLoadPhaseEnum.REMOVE.getPhase());
                    phase1Map.put(node.getAddress(), node.getDataVersion(), tmpExecutorTaskStateBO);
                    List<DeferredResultWrapper<ExecutorTaskStateBO>> deferredResultWrapperList = deferredResults.stream().filter(e -> e.getInstanceId().equals(node.getAddress())).collect(Collectors.toList());
                    for (DeferredResultWrapper<ExecutorTaskStateBO> deferredResultWrapper : deferredResultWrapperList) {
                        deferredResultWrapper.setResult(phase1Map.get(deferredResultWrapper.getInstanceId(), node.getDataVersion()));
                    }
                    log.info("分配结果为 address:{} result:{}", node.getAddress(), tmpExecutorTaskStateBO);
                }
                if (CollectionUtils.isNotEmpty(executorTaskStateBO.getNeedStartTaskList())) {
                    ExecutorTaskStateBO tmpExecutorTaskStateBO = BeanUtils.copyBean(ExecutorTaskStateBO.class, executorTaskStateBO);
                    tmpExecutorTaskStateBO.setExecutorPhase(ExecutorLoadPhaseEnum.ADD.getPhase());
                    phase2Map.put(node.getAddress(), node.getDataVersion(), tmpExecutorTaskStateBO);
                    log.info("分配结果为 address:{} result:{}", node.getAddress(), tmpExecutorTaskStateBO);
                }
            }
            if (phase1Map.isEmpty()) {
                Map<String, String> nodeVersionMap = executorNodeList.stream().collect(Collectors.toMap(e -> e.getAddress(), e -> e.getDataVersion()));
                for (DeferredResultWrapper<ExecutorTaskStateBO> deferredResultWrapper : deferredResults) {
                    String version = nodeVersionMap.get(deferredResultWrapper.getInstanceId());
                    if (StringUtils.isNotBlank(version) && phase2Map.get(deferredResultWrapper.getInstanceId(), version) != null) {
                        executorTaskStateTable.get(deferredResultWrapper.getInstanceId(), version).setExecutorPhase(ExecutorLoadPhaseEnum.ADD.getPhase());
                        deferredResultWrapper.setResult(phase2Map.get(deferredResultWrapper.getInstanceId(), version));
                    }
                }
            }
        } catch (InterruptedException e) {
        } catch (Exception e) {
            log.error("任务分配异常 ", e);
        }
    }

    private void ergodicAssign(String tag, List<TaskEntity> taskEntityList, List<Node> tmpExecutorList) {
        if (CollectionUtils.isEmpty(taskEntityList) || CollectionUtils.isEmpty(tmpExecutorList)) {
            return;
        }
        //执行机总数
        int totalExecNum = tmpExecutorList.size();
        //平均每台机器分配任务数 不能使用任务数,会导致分配不完整
        int totalTaskNum = taskEntityList.stream().mapToInt(TaskEntity::getAssignNum).sum();
        if (totalTaskNum == 0) {
            return;
        }
        int executorAvgTaskNum = totalTaskNum / totalExecNum;
        int executorRdTaskNum = totalTaskNum % totalExecNum;
        int executorMaxTaskNum = executorAvgTaskNum + (executorRdTaskNum > 0 ? 1 : 0);
        List<TaskEntity> todoTaskList = new ArrayList<>(taskEntityList);
        List<TaskAssignResultEntity> todoTaskAssignResultList = convert(todoTaskList, totalExecNum, totalTaskNum, executorMaxTaskNum);

        // 待分配任务表格 row taskName,column seqNo
        Table<String, Integer, TaskAssignResultEntity> todoTaskAssignResultTable = HashBasedTable.create();
        for (TaskAssignResultEntity taskAssignResultEntity : todoTaskAssignResultList) {
            todoTaskAssignResultTable.put(taskAssignResultEntity.getTaskName(), taskAssignResultEntity.getSeqNo(), taskAssignResultEntity);
        }
        Table<String, String, TaskAssignResultEntity> needStartTaskTable = HashBasedTable.create();
        Table<String, String, TaskAssignResultEntity> needStopTaskTable = HashBasedTable.create();

        //将历史分配结果映射为一个table  row address,column index
        Table<String, Integer, TaskAssignResultEntity> hisAssignTable = convertToHisAssignTable(tmpExecutorList);
        // 历史-> 当前分配结果 遗留 + (新增的AssignNum or 新增的task)
        // 保持队形
        List<TaskAssignResultEntity> todoAssignList;
        Table<String, Integer, TaskAssignResultEntity> assignResultTable;
        if (CollectionUtils.isEmpty(hisAssignTable.values())) {
            assignResultTable = HashBasedTable.create();
            todoAssignList = new ArrayList<>(todoTaskAssignResultList);
        } else {
            // 将历史分配结果复制到新表格 并且进行裁剪
            assignResultTable = convertToNewAssignResultTable(hisAssignTable, todoTaskAssignResultTable, needStartTaskTable, needStopTaskTable, tmpExecutorList, executorMaxTaskNum);
            todoAssignList = new ArrayList<>(hisAssignTable.values());
            todoAssignList.addAll(todoTaskAssignResultTable.values());
        }

        for (Node node : tmpExecutorList) {
            int maxIndex = assignResultTable.row(node.getAddress()).keySet().stream().max(Comparator.comparingInt(e -> e)).orElse(-1);
            while (assignResultTable.get(node.getAddress(), executorMaxTaskNum - 1) == null && CollectionUtils.isNotEmpty(todoAssignList)) {
                TaskAssignResultEntity addAssign = todoAssignList.remove(0);
                addAssign.setExecutorAddress(node.getAddress());
                if (addAssign.getAssignNum() > 0) {
                    needStartTaskTable.put(node.getAddress(), getColumnKey(addAssign), addAssign);
                }
                assignResultTable.put(node.getAddress(), maxIndex + 1, addAssign);
                maxIndex++;
            }
        }

        for (Node node : tmpExecutorList) {
            String address = node.getAddress();
            ExecutorTaskStateBO executorTaskStateBO = executorTaskStateTable.get(address, node.getDataVersion());
            executorTaskStateBO.setTag(tag);
            List<TaskAssignResultEntity> taskAssignResultList = new ArrayList<>(assignResultTable.row(address).values());
            executorTaskStateBO.setTaskAssignResultList(taskAssignResultList);
            if (CollectionUtils.isNotEmpty(needStartTaskTable.row(address).values()) || CollectionUtils.isNotEmpty(needStopTaskTable.row(address).values())) {
                executorTaskStateBO.setExecutorPhase(ExecutorLoadPhaseEnum.RESET.getPhase());
                executorTaskStateBO.setExecutorState(ExecutorLoadStateEnum.RESET.getState());
                executorTaskStateBO.setNeedStopTaskList(new ArrayList<>(needStopTaskTable.row(address).values()));
                executorTaskStateBO.setNeedStartTaskList(new ArrayList<>(needStartTaskTable.row(address).values()));
            }
        }
    }

    private Table<String, Integer, TaskAssignResultEntity> convertToNewAssignResultTable(Table<String, Integer, TaskAssignResultEntity> hisAssignTable, Table<String, Integer, TaskAssignResultEntity> todoTaskAssignResultTable, Table<String, String, TaskAssignResultEntity> needStartTaskTable, Table<String, String, TaskAssignResultEntity> needStopTaskTable, List<Node> tmpExecutorList, int executorMaxTaskNum) {
        Table<String, Integer, TaskAssignResultEntity> assignResultTable = HashBasedTable.create();

        for (Node node : tmpExecutorList) {
            Map<Integer, TaskAssignResultEntity> hisRow = hisAssignTable.row(node.getAddress());
            if (hisRow == null || hisRow.isEmpty()) {
                continue;
            }
            int currentNodeTotalTask = 0;
            for (int i = 0, hisSize = hisRow.size(); i < hisSize; i++) {
                TaskAssignResultEntity hisAssign = hisRow.remove(i);
                if (hisAssign == null) {
                    break;
                }
                // 裁剪
                if (currentNodeTotalTask >= executorMaxTaskNum) {
                    needStopTaskTable.put(node.getAddress(), getColumnKey(hisAssign), hisAssign);
                    continue;
                }
                TaskAssignResultEntity todoAssignItem = todoTaskAssignResultTable.remove(hisAssign.getTaskName(), hisAssign.getSeqNo());
                if (todoAssignItem != null) {
                    todoAssignItem.setExecutorAddress(node.getAddress());
                    // 状态停止 需要移除
                    if (hisAssign.getAssignNum() > todoAssignItem.getAssignNum()) {
                        needStopTaskTable.put(node.getAddress(), getColumnKey(hisAssign), hisAssign);
                    } else if (hisAssign.getAssignNum() < todoAssignItem.getAssignNum()) {
                        // 状态启动 需要补充
                        needStartTaskTable.put(node.getAddress(), getColumnKey(todoAssignItem), todoAssignItem);
                    } else if (!Objects.equals(hisAssign.getTaskItems(), todoAssignItem.getTaskItems())) {
                        // 任务项发生变更需要重新加载
                        needStopTaskTable.put(node.getAddress(), getColumnKey(hisAssign), hisAssign);
                        needStartTaskTable.put(node.getAddress(), getColumnKey(todoAssignItem), todoAssignItem);
                    }
                    assignResultTable.put(node.getAddress(), i, todoAssignItem);
                    currentNodeTotalTask++;
                } else {
                    //assignNum缩小 or 任务删除 需要移除
                    needStopTaskTable.put(node.getAddress(), getColumnKey(hisAssign), hisAssign);
                }
            }
        }
        return assignResultTable;
    }

    private Table<String, Integer, TaskAssignResultEntity> convertToHisAssignTable(List<Node> tmpExecutorList) {
        Table<String, Integer, TaskAssignResultEntity> hisAssignTable = HashBasedTable.create();
        Map<String, ExecutorTaskStateBO> executorTaskStateMap = new HashMap<>();
        for (Node node : tmpExecutorList) {
            executorTaskStateMap.put(node.getAddress(), executorTaskStateTable.get(node.getAddress(), node.getDataVersion()));
        }
        for (Map.Entry<String, ExecutorTaskStateBO> executorTaskStateBOEntry : executorTaskStateMap.entrySet()) {
            for (int i = 0; i < executorTaskStateBOEntry.getValue().getTaskAssignResultList().size(); i++) {
                TaskAssignResultEntity hisAssign = executorTaskStateBOEntry.getValue().getTaskAssignResultList().get(i);
                hisAssign.setExecutorAddress(executorTaskStateBOEntry.getKey());
                hisAssignTable.put(executorTaskStateBOEntry.getKey(), i, hisAssign);
            }
        }
        return hisAssignTable;
    }

    private String getColumnKey(TaskAssignResultEntity taskAssignResultEntity) {
        return taskAssignResultEntity.getTaskName() + "#" + taskAssignResultEntity.getSeqNo();
    }


    private List<TaskAssignResultEntity> convert(List<TaskEntity> todoTaskList, int totalExecNum, int totalTaskNum, int executorMaxTaskNum) {
        List<TaskAssignResultEntity> taskAssignResultEntityList = new ArrayList<>(totalTaskNum);
        Map<Integer, List<Integer>> indexMap = new HashMap<>(totalExecNum);
        for (int i = 0; i < totalExecNum; i++) {
            List<Integer> indexList = new ArrayList<>();
            for (int j = 0; j < executorMaxTaskNum; j++) {
                if ((i * executorMaxTaskNum) + j < totalTaskNum) {
                    indexList.add((i * executorMaxTaskNum) + j);
                    taskAssignResultEntityList.add(null);
                } else {
                    break;
                }
            }
            indexMap.put(i, indexList);
        }

        Collections.sort(todoTaskList, Comparator.comparing((e1) -> e1.getAssignNum() * (Objects.equals(ChronusConstants.STS_NORMAL, e1.getState()) ? -1 : 1)));

        int execIndex = 0;
        for (TaskEntity taskEntity : todoTaskList) {
            for (int i = 0; i < taskEntity.getAssignNum(); i++) {
                TaskAssignResultEntity taskAssignResultEntity = new TaskAssignResultEntity();
                taskAssignResultEntity.setCluster(currentNode.getCluster());
                taskAssignResultEntity.setMasterVersion(currentNode.getVersion());
                taskAssignResultEntity.setTaskName(taskEntity.getTaskName());
                taskAssignResultEntity.setSeqNo(i);
                taskAssignResultEntity.setAssignNum(Objects.equals(ChronusConstants.STS_NORMAL, taskEntity.getState()) ? 1 : 0);
                taskAssignResultEntity.setTaskItems(String.join(",", taskItemAssign(getTaskItems(taskEntity), taskEntity.getAssignNum(), i)));
                taskAssignResultEntity.setState(TaskCreateStateEnum.INIT.getState());
                int oldExecIndex = execIndex;
                if (i > 0) {
                    execIndex = execIndex + 1 >= totalExecNum ? 0 : execIndex + 1;
                }
                while (CollectionUtils.isEmpty(indexMap.get(execIndex))) {
                    execIndex = execIndex + 1 >= totalExecNum ? 0 : execIndex + 1;
                }
                boolean changeFlag = oldExecIndex != execIndex;
                List<Integer> tmpIndexList = indexMap.get(execIndex);
                int index = tmpIndexList.remove(changeFlag ? 0 : tmpIndexList.size() - 1);
                taskAssignResultEntityList.set(index, taskAssignResultEntity);
            }
        }
        return taskAssignResultEntityList;
    }

    private Map<String, List<TaskEntity>> getTagTaskListMap(List<TaskEntity> taskList) {
        return taskList.stream().collect(Collectors.groupingBy(e -> e.getTag()));
    }

    private List<String> getTaskItems(TaskEntity taskEntity) {
        if (StringUtils.isBlank(taskEntity.getTaskItems())) {
            return null;
        }
        return Lists.newArrayList(taskEntity.getTaskItems().split(","));
    }

    private List<String> taskItemAssign(List<String> taskItems, int assignNum, int ci) {
        List<String> result = new ArrayList<>();
        if (CollectionUtils.isEmpty(taskItems)) {
            return result;
        }
        if (taskItems.size() <= ci) {
            return result;
        }
        int minItemNums = taskItems.size() / assignNum;
        int bcItemNums = taskItems.size() % assignNum;
        if (minItemNums == 0) {
            result.addAll(new ArrayList<>(taskItems.subList(ci, (ci + 1))));
        } else if (bcItemNums == 0) {
            result.addAll(new ArrayList<>(taskItems.subList(ci * minItemNums, (ci + 1) * minItemNums)));
        } else if (bcItemNums > ci) {
            result.addAll(new ArrayList<>(taskItems.subList((ci * minItemNums) + ci, ((ci + 1) * minItemNums) + ci + 1)));
        } else if (bcItemNums <= ci) {
            result.addAll(new ArrayList<>(taskItems.subList((ci * minItemNums) + bcItemNums, ((ci + 1) * minItemNums) + bcItemNums)));
        }
        return result;
    }
}
