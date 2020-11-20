package com.qihoo.finance.chronus.master.service;

import com.google.common.collect.*;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.enums.TaskStateEnum;
import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.cluster.ClusterService;
import com.qihoo.finance.chronus.core.log.annotation.NodeLog;
import com.qihoo.finance.chronus.core.log.bo.NodeLogAssignTask;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.core.task.TaskItemService;
import com.qihoo.finance.chronus.core.task.TaskService;
import com.qihoo.finance.chronus.master.bo.TaskAssignContext;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskItemStateEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.WorkerLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.metadata.api.common.enums.ClusterStateEnum;
import com.qihoo.finance.chronus.metadata.api.common.enums.NodeStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/9/16.
 */
@Slf4j
public class TaskAssignService {
    @Resource
    private TaskService taskService;
    @Resource
    private NodeInfo currentNode;
    @Resource
    private NamingService namingService;
    @Resource
    private TaskItemService taskItemService;
    @Resource
    private ClusterService clusterService;
    private Map<String, List<Node>> tagWorkerMap;
    private boolean needAssign = true;
    private ClusterEntity currentClusterEntity;
    //当前集群的所有节点列表
    private List<Node> workerNodeList;
    //当前集群的所有正常节点列表
    private List<Node> workerNormalNodeList;
    //所有状态为正常的节点地址列表
    private Map<String, Node> allNodeAddressMap;
    //当前集群需要重新分配受影响的任务列表
    private List<TaskEntity> taskList;
    //key taskName
    private Map<String, TaskEntity> allTaskMap = new HashMap<>();
    private Table<String, Integer, TaskItemEntity> todoTaskAssignTable = HashBasedTable.create();
    private Table<String, Integer, TaskItemEntity> taskItemTable = HashBasedTable.create();
    private Map<String, NodeLogAssignTask> nodeLogAssignTaskMap = new HashMap<>();

    public static TaskAssignService create() {
        return SpringContextHolder.getBean(TaskAssignService.class);
    }

    /**
     * 初始化执行机列表,任务列表,任务变更记录
     *
     * @return
     * @throws Exception
     */
    public TaskAssignService init() throws Exception {
        this.currentClusterEntity = clusterService.selectByCluster(currentNode.getCluster());
        if (this.currentClusterEntity == null) {
            log.error("集群:{},配置不存在,请检查! ", currentNode.getCluster());
        }

        // 当前集群的所有任务列表
        this.taskList = taskService.selectAllTaskInfo();

        if (CollectionUtils.isEmpty(this.taskList)) {
            log.info("不存在调度任务 跳过处理!");
            this.stopAssign();
            return this;
        }
        long taskStartCount = this.taskList.stream().filter(e -> TaskStateEnum.START.name().equals(e.getState())).count();
        log.info("获取到{}个任务,启动状态任务{}个", this.taskList.size(), taskStartCount);
        // key 任务名称 value 任务配置信息
        this.allTaskMap = taskList.stream().collect(Collectors.toMap(e -> e.getDealSysCode() + e.getTaskName(), e -> e));
        // 当前集群的所有worker节点
        this.workerNodeList = namingService.getAllWorkerNode();
        if (CollectionUtils.isEmpty(this.workerNodeList)) {
            log.error("任务所属的集群:{},不存在执行机,请检查! ", currentNode.getCluster());
            this.stopAssign();
            return this;
        }
        this.workerNormalNodeList = this.workerNodeList.stream().filter(e -> NodeStateEnum.isNormal(e.getState())).collect(Collectors.toList());
        this.allNodeAddressMap = this.workerNodeList.stream().collect(Collectors.toMap(e -> e.getAddress(), e -> e));
        this.tagWorkerMap = this.workerNormalNodeList.stream().collect(Collectors.groupingBy(e -> e.getTag()));

        for (Node node : this.workerNormalNodeList) {
            WorkerTaskStateBO workerTaskStateBO = TaskAssignContext.getTaskState(node.getAddress(), node.getDataVersion());
            if (workerTaskStateBO == null) {
                log.info("节点:{}还未上报加载状态, 跳过本次任务分配,等待上报完成继续分配!", node.getAddress());
                this.stopAssign();
                return this;
            }
            if (!WorkerLoadStateEnum.READY.name().equals(workerTaskStateBO.getWorkerState())) {
                log.info("存在节点:{} state:{},任务还在加载中, 跳过本次任务分配,待其处理完成继续分配!", node.getAddress(), workerTaskStateBO.getWorkerState());
                this.stopAssign();
                return this;
            }
            log.info("worker node:{} is ready", node.getAddress());
        }
        return this;
    }


    @NodeLog(value = NodeLogTypeEnum.MASTER_TASK_ASSIGN, resultPutContent = true)
    public Collection<NodeLogAssignTask> taskAssign() {
        try {
            log.info("cluster:{} 开始进行任务分配!", currentNode.getCluster());
            // 初始化所有任务信息的任务运行信息
            initTaskItemInfo();

            TaskAssignContext.taskAssignBeforeClear();
            // 当前节点&任务映射关系
            Multimap<String, TaskItemEntity> nodeTaskMapping = ArrayListMultimap.create();
            // 对初始状态 或者 所在节点已经下线的任务 放入待处理列表
            for (TaskItemEntity taskItemEntity : taskItemTable.values()) {
                if (StringUtils.isNotBlank(taskItemEntity.getWorkerAddress())) {
                    nodeTaskMapping.put(taskItemEntity.getWorkerAddress(), taskItemEntity);
                }
                TaskEntity taskEntity = allTaskMap.get(taskItemEntity.getDealSysCode() + taskItemEntity.getTaskName());
                if (taskEntity == null || !Objects.equals(currentNode.getCluster(), taskEntity.getCluster())) {
                    continue;
                }
                if (TaskItemStateEnum.isInit(taskItemEntity.getState())) {
                    add2NeedAssignTable(taskEntity, taskItemEntity);
                    continue;
                }
                Node taskExecNode = allNodeAddressMap.get(taskItemEntity.getWorkerAddress());
                if (taskExecNode == null) {
                    log.info("任务:{}所在节点:{}已离线!任务准备转移!", taskItemEntity.getTaskItemId(), taskItemEntity.getWorkerAddress());
                    add2NeedAssignTable(taskEntity, taskItemEntity);
                } else {
//                    // 集群关闭,如果任务正在当前集群运行,任务销毁
//                    if (ClusterStateEnum.isClose(currentClusterEntity.getClusterState())) {
//                        if (Objects.equals(currentNode.getCluster(), taskItemEntity.getCluster())) {
//                            log.info("启动容灾,任务准备销毁转移! currentNodeCluster:{}", currentNode.getCluster());
//                            destroyTask(taskItemEntity);
//                        }
//                        continue;
//                    }
//
//                    //正常状态的集群 只处理优先集群一致的任务, 如果集群状态正常, 任务运行状态为INIT, 则按原有逻辑处理
//                    if (ClusterStateEnum.isNormal(currentClusterEntity.getClusterState()) && !TaskItemStateEnum.isInit(taskItemEntity.getState())) {
//                        // 优先集群一致
//                        if (Objects.equals(currentNode.getCluster(), taskEntity.getCluster())) {
//                            // 运行集群如果不一致,意思是 属于当前集群的任务 在其他集群运行(容灾恢复未转移)
//                            if (!Objects.equals(currentNode.getCluster(), taskItemEntity.getCluster())) {
//                                log.warn("任务:{}所在节点:{},{}长时间未释放任务,任务放入待分配队列!", taskItemEntity.getTaskItemId(), taskItemEntity.getWorkerAddress(), taskItemEntity.getCluster());
//                                add2NeedAssignTable(taskEntity, taskItemEntity);
//                                continue;
//                            }
//                            //else 运行集群如果一致,则按正常逻辑判断
//                        } else {
//                            //优先集群不一致,运行集群如果一致,属于其他集群的任务,我正在运行,需要销毁任务
//                            if (Objects.equals(currentNode.getCluster(), taskItemEntity.getCluster())) {
//                                log.info("容灾恢复,任务释放,开始销毁:{},优先集群:{},当前集群:{}", taskItemEntity.getTaskItemId(), taskEntity.getCluster(), taskItemEntity.getCluster());
//                                destroyTask(taskItemEntity);
//                            }
//                            continue;
//                        }
//                    }
//                    // 容灾状态的集群,忽略优先集群
//                    if (ClusterStateEnum.isDataGuard(currentClusterEntity.getClusterState())) {
//                        // 运行集群如果不一致,意思是 属于当前集群的任务 在其他集群运行(容灾启动未转移)
//                        // 正常情况下,正在运行的节点会主动销毁当前正在运行的任务,不会出现这个状态
//                        if (!Objects.equals(currentNode.getCluster(), taskItemEntity.getCluster())) {
//                            log.warn("任务:{}所在节点:{},{}长时间未释放任务,任务放入待分配队列!", taskItemEntity.getTaskItemId(), taskItemEntity.getWorkerAddress(), taskItemEntity.getCluster());
//                            add2NeedAssignTable(taskEntity, taskItemEntity);
//                            continue;
//                        }
//                    }
                }
            }

            // 遍历所有节点
            Multimap<String, TaskItemEntity> taskAssignResultMultimap = getTagTaskRuntimeMap();
            for (Map.Entry<String, List<Node>> entry : tagWorkerMap.entrySet()) {
                String tag = entry.getKey();
                List<Node> workerNodeList = entry.getValue();
                long totalStartTaskNum = getTagTotalStartTask(tag);
                long workerMaxTaskNum = getWorkerMaxTaskNum(totalStartTaskNum, workerNodeList.size());
                List<TaskItemEntity> todoTaskAssignResultList = new ArrayList<>(taskAssignResultMultimap.get(tag));
                log.info("开始均衡 tag:{} 下所有节点的任务! 总启动状态任务数:{},平均每台机器任务数:{}", tag, totalStartTaskNum, workerMaxTaskNum);

                for (Node node : workerNodeList) {
                    List<TaskItemEntity> nodeAllTaskItems = new ArrayList<>(CollectionUtils.emptyIfNull(nodeTaskMapping.get(node.getAddress())));
                    for (int index = 0; index < nodeAllTaskItems.size(); index++) {
                        TaskItemEntity taskItemEntity = nodeAllTaskItems.get(index);
                        TaskEntity taskEntity = allTaskMap.get(taskItemEntity.getDealSysCode() + taskItemEntity.getTaskName());
                        if (taskEntity == null) {
                            log.info("任务:{}被删除,任务准备销毁!", taskItemEntity.getTaskItemId());
                            destroyTask(taskItemEntity);
                            continue;
                        }
                        // 任务所在执行机节点重启过
                        boolean nodeRestart = StringUtils.isNotBlank(taskItemEntity.getWorkerAddress()) && !Objects.equals(taskItemEntity.getWorkerVersion(), node.getDataVersion());
                        if (nodeRestart) {
                            log.info("destroyTask:{} nodeRestart:true", taskItemEntity.getTaskItemId());
                            destroyTask(taskItemEntity);
                            continue;
                        }

                        // 任务更改过
                        boolean taskUpdate = !DateUtils.toDateTimeText(taskEntity.getDateUpdated()).equals(DateUtils.toDateTimeText(taskItemEntity.getTaskDateUpdated()));
                        if (taskUpdate) {
                            log.info("destroyTask:{} taskUpdate:true", taskItemEntity.getTaskItemId());
                            if (taskItemEntity.getSeqNo() >= taskEntity.getAssignNum()) {
                                log.info("任务变更:{}被缩减线程组,任务准备销毁!", taskItemEntity.getTaskItemId());
                            }
                            if (Objects.equals(TaskStateEnum.STOP.name(), taskEntity.getState()) && TaskItemStateEnum.isStart(taskItemEntity.getState()) && TaskAssignContext.getTaskState(taskItemEntity.getWorkerAddress()) != null) {
                                log.info("任务变更:{}被停止,任务准备销毁!", taskItemEntity.getTaskItemId());
                            }
                            destroyTask(taskItemEntity);
                            continue;
                        }
                    }

                    if (workerNodeList.size() > 1 && getCurrentNodeTaskItemSize(nodeAllTaskItems) > workerMaxTaskNum + 10) {
                        List<TaskItemEntity> removedTaskItems = nodeAllTaskItems.stream().sorted(Comparator.comparingLong(e -> e.getDateUpdated().getTime() * -1)).collect(Collectors.toList());
                        for (int i = 0; i < 10; i++) {
                            TaskItemEntity taskItemEntity = removedTaskItems.get(i);
                            if (!TaskItemStateEnum.isWaitStop(taskItemEntity.getState()) && !TaskItemStateEnum.isStop(taskItemEntity.getState())) {
                                log.info("任务:{}超过节点最大任务数量:{},任务准备销毁!", taskItemEntity.getTaskItemId(), workerMaxTaskNum);
                                destroyTask(taskItemEntity);
                            }
                        }
                    }
                }
                for (Node node : workerNodeList) {
                    List<TaskItemEntity> nodeAllTaskItems = new ArrayList<>(CollectionUtils.emptyIfNull(nodeTaskMapping.get(node.getAddress())));
                    long currentSize = getCurrentNodeTaskItemSize(nodeAllTaskItems);
                    if (currentSize < workerMaxTaskNum) {
                        WorkerTaskStateBO workerTaskStateBO = TaskAssignContext.getTaskState(node.getAddress(), node.getDataVersion());
                        List<TaskItemEntity> taskAssignResultItemList = new ArrayList<>();
                        for (long i = 0; i < workerMaxTaskNum - currentSize && CollectionUtils.isNotEmpty(todoTaskAssignResultList); i++) {
                            taskAssignResultItemList.add(todoTaskAssignResultList.remove(0));
                        }
                        for (TaskItemEntity taskItemEntity : taskAssignResultItemList) {
                            TaskEntity taskEntity = allTaskMap.get(taskItemEntity.getDealSysCode() + taskItemEntity.getTaskName());
                            restartTask(node, workerTaskStateBO, taskEntity, taskItemEntity);
                        }
                    }
                }
            }

            //3.打印分配结果
            for (Node node : this.workerNodeList) {
                WorkerTaskStateBO workerTaskStateBO = TaskAssignContext.getTaskState(node.getAddress(), node.getDataVersion());
                if (workerTaskStateBO == null || MapUtils.isEmpty(workerTaskStateBO.getAssignChangeMap())) {
                    continue;
                }
                WorkerTaskStateBO p1WorkerTaskStateBO = BeanUtils.copyBean(WorkerTaskStateBO.class, workerTaskStateBO);
                TaskAssignContext.assignResultMapPut(node.getAddress(), node.getDataVersion(), p1WorkerTaskStateBO);
                p1WorkerTaskStateBO.reset();
                workerTaskStateBO.reset();
                log.info("分配结果为 address:{} assignChangeMap:{}", node.getAddress(), p1WorkerTaskStateBO.getAssignChangeMap());
            }
        } catch (Exception e) {
            log.error("任务分配异常", e);
        }
        return nodeLogAssignTaskMap.values();
    }

    private void initTaskItemInfo() {
        List<TaskItemEntity> taskItemEntityList = taskItemService.selectAllTaskItem();
        for (TaskItemEntity taskItemEntity : taskItemEntityList) {
            taskItemTable.put(taskItemEntity.getTaskName(), taskItemEntity.getSeqNo(), taskItemEntity);
        }
        // 如果集群关闭 则不处理新任务
        if (ClusterStateEnum.isClose(currentClusterEntity.getClusterState())) {
            return;
        }
        for (TaskEntity taskEntity : taskList) {
            for (int i = 0; i < taskEntity.getAssignNum(); i++) {
                boolean flag = taskItemTable.contains(taskEntity.getTaskName(), i);
                if (!flag && Objects.equals(TaskStateEnum.START.name(), taskEntity.getState())) {
                    TaskItemEntity taskItemEntity = new TaskItemEntity(taskEntity, i);
                    taskItemEntity.setTaskItems(String.join(",", taskItemAssign(getTaskItems(taskEntity), taskEntity.getAssignNum(), i)));
                    taskItemEntity.setMasterAddress(currentNode.getAddress());
                    taskItemEntity.setMasterVersion(currentNode.getVersion());
                    taskItemEntity.setCluster(currentNode.getCluster());
                    taskItemEntity.setDateUpdated(new Date());
                    taskItemTable.put(taskEntity.getTaskName(), i, taskItemEntity);
                }
            }
        }
    }

    private long getTagTotalStartTask(String tag) {
        return this.taskList.stream().filter(e -> tag.equals(e.getTag()) && TaskStateEnum.START.name().equals(e.getState())).mapToLong(e -> Long.valueOf(e.getAssignNum())).sum();
    }

    private Multimap<String, TaskItemEntity> getTagTaskRuntimeMap() {
        Multimap<String, TaskItemEntity> tagTaskMapping = ArrayListMultimap.create();
        for (TaskItemEntity taskItemEntity : todoTaskAssignTable.values()) {
            tagTaskMapping.put(taskItemEntity.getTag(), taskItemEntity);
        }
        return tagTaskMapping;
    }

    private void stopAssign() {
        this.needAssign = false;
    }

    public boolean isNeedAssign() {
        return needAssign;
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

    private long getWorkerMaxTaskNum(long totalTaskNum, int workerNodeListSize) {
        long workerAvgTaskNum = totalTaskNum / workerNodeListSize;
        long workerRdTaskNum = totalTaskNum % workerNodeListSize;
        return workerAvgTaskNum + (workerRdTaskNum > 0 ? 1 : 0);
    }

    private long getCurrentNodeTaskItemSize(List<TaskItemEntity> nodeAllTaskItems) {
        return nodeAllTaskItems.stream().filter(e -> !TaskItemStateEnum.isWaitStop(e.getState()) && !TaskItemStateEnum.isStop(e.getState())).count();
    }


    /**
     * 销毁任务,从原来的执行机上移除
     *
     * @param taskItemEntity
     */
    private void destroyTask(TaskItemEntity taskItemEntity) {
        if (TaskItemStateEnum.isStop(taskItemEntity.getState())) {
            return;
        }

        WorkerTaskStateBO workerTaskStateBO = TaskAssignContext.getTaskState(taskItemEntity.getWorkerAddress());
        if (workerTaskStateBO != null) {
            log.info("检测到任务{},需要从{}上销毁!", taskItemEntity.getTaskItemId(), taskItemEntity.getWorkerAddress());
            workerTaskStateBO.init();
            taskItemEntity.setState(TaskItemStateEnum.WAIT_STOP.name());
            workerTaskStateBO.put2DestroyTaskMap(taskItemEntity.getTaskItemId(), taskItemEntity);
            nodeLogAssignTaskMap.put(taskItemEntity.getTaskItemId(), new NodeLogAssignTask(taskItemEntity, null));
        } else {
            log.info("检测到任务{},所在节点{}已经丢失!", taskItemEntity.getTaskItemId(), taskItemEntity.getWorkerAddress());
        }
    }

    /**
     * 需要重新分配启动的任务(在其他节点上重新启动)
     */
    private void restartTask(Node node, WorkerTaskStateBO workerTaskStateBO, TaskEntity taskEntity, TaskItemEntity taskItemEntity) {
        log.info("检测到任务{},需要从{}启动!", taskItemEntity.getTaskItemId(), node.getAddress());
        String currentState = taskItemEntity.getState();
        taskItemEntity.setState(TaskItemStateEnum.WAIT_START.name());
        boolean lockFlag = true;
        if (TaskItemStateEnum.isInit(currentState) && StringUtils.isBlank(taskItemEntity.getWorkerAddress())) {
            taskItemEntity.init(node.getAddress(), node.getDataVersion());
            taskItemService.create(taskItemEntity);
        } else {
            taskItemEntity.init(node.getAddress(), node.getDataVersion());
            reInit(taskEntity, taskItemEntity);
            lockFlag = taskItemService.restartLock(taskItemEntity);
        }
        if (lockFlag) {
            workerTaskStateBO.put2StartTaskMap(taskItemEntity.getTaskItemId(), taskItemEntity);
            NodeLogAssignTask nodeLogAssignTask = nodeLogAssignTaskMap.getOrDefault(taskItemEntity.getTaskItemId(), new NodeLogAssignTask(taskItemEntity, taskItemEntity.getWorkerAddress()));
            nodeLogAssignTaskMap.put(taskItemEntity.getTaskItemId(), nodeLogAssignTask);
        } else {
            log.warn("任务分配,获取锁更新失败:{}", taskItemEntity);
        }
    }

    /**
     * 将任务项放入待分配列表
     *
     * @param taskEntity
     * @param taskItemEntity
     */
    private void add2NeedAssignTable(TaskEntity taskEntity, TaskItemEntity taskItemEntity) {
        reInit(taskEntity, taskItemEntity);
        todoTaskAssignTable.put(taskItemEntity.getTaskName(), taskItemEntity.getSeqNo(), taskItemEntity);
    }

    private void reInit(TaskEntity taskEntity, TaskItemEntity taskItemEntity) {
        taskItemEntity.setTaskDateUpdated(taskEntity.getDateUpdated());
        //兼容直接从数据库获取的taskItem,但是任务发生了变更的情况
        taskItemEntity.setTaskItems(String.join(",", taskItemAssign(getTaskItems(taskEntity), taskEntity.getAssignNum(), taskItemEntity.getSeqNo())));
        taskItemEntity.setMasterAddress(currentNode.getAddress());
        taskItemEntity.setMasterVersion(currentNode.getVersion());
    }
}
