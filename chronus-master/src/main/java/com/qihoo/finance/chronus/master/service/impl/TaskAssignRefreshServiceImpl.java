package com.qihoo.finance.chronus.master.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.core.task.TaskItemService;
import com.qihoo.finance.chronus.master.bo.DeferredResultWrapper;
import com.qihoo.finance.chronus.master.bo.TaskAssignContext;
import com.qihoo.finance.chronus.master.service.TaskAssignRefreshService;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskItemStateEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.WorkerLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.async.DeferredResult;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service("taskAssignRefreshService")
public class TaskAssignRefreshServiceImpl implements TaskAssignRefreshService {
    @Resource
    private TaskItemService taskItemService;
    private static ScheduledExecutorService REFRESH_TASK_ASSIGN_RESULT = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_WORKER, SupportConstants.REFRESH_TASK_ASSIGN_RESULT));
    private static ScheduledFuture REFRESH_TASK_ASSIGN_RESULT_FUTURE;
    private volatile static Table<String, String, DeferredResultWrapper<WorkerTaskStateBO>> deferredResults = HashBasedTable.create();

    @Override
    public void restartRefreshTask() {
        shutdownRefreshTask();
        REFRESH_TASK_ASSIGN_RESULT_FUTURE = REFRESH_TASK_ASSIGN_RESULT.scheduleWithFixedDelay(refreshTask, 3, 1, TimeUnit.SECONDS);
    }

    @Override
    public void shutdownRefreshTask() {
        if (REFRESH_TASK_ASSIGN_RESULT_FUTURE != null && !REFRESH_TASK_ASSIGN_RESULT_FUTURE.isCancelled()) {
            REFRESH_TASK_ASSIGN_RESULT_FUTURE.cancel(false);
        }
    }

    /**
     * 节点任务发生了变更 则返回WorkerLoadStateEnum.RESET
     */
    Runnable refreshTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_WORKER, "refreshTask", false) {
        @Override
        public void process() {
            Iterator<DeferredResultWrapper<WorkerTaskStateBO>> iterator = TaskAssignRefreshServiceImpl.getDeferredResultWrappers().iterator();
            while (iterator.hasNext()) {
                DeferredResultWrapper<WorkerTaskStateBO> deferredResultWrapper = iterator.next();
                String address = deferredResultWrapper.getInstanceId();
                String dataVersion = deferredResultWrapper.getVersion();
                WorkerTaskStateBO workerTaskStateBO = TaskAssignContext.getTaskState(address, dataVersion);
                if (workerTaskStateBO != null && WorkerLoadStateEnum.RESET.name().equals(workerTaskStateBO.getWorkerState()) && TaskAssignContext.assignResultMapGet(address, dataVersion) != null) {
                    deferredResultWrapper.setResult(TaskAssignContext.assignResultMapGet(address, dataVersion));
                    iterator.remove();
                }
            }
        }
    };

    private static Collection<DeferredResultWrapper<WorkerTaskStateBO>> getDeferredResultWrappers() {
        return deferredResults.values();
    }

    private static void putDeferredResultWrapper(DeferredResultWrapper<WorkerTaskStateBO> deferredResultWrapper) {
        deferredResults.put(deferredResultWrapper.getInstanceId(), deferredResultWrapper.getVersion(), deferredResultWrapper);
    }

    @Override
    public DeferredResult<ResponseEntity<WorkerTaskStateBO>> pullAssignResult(String address, String dataVersion) {
        log.info("接收到 address:{} dataVersion:{}获取分配结果请求!", address, dataVersion);
        DeferredResultWrapper<WorkerTaskStateBO> deferredResultWrapper = new DeferredResultWrapper(address, dataVersion);
        TaskAssignRefreshServiceImpl.putDeferredResultWrapper(deferredResultWrapper);
        return deferredResultWrapper.getResult();
    }

    @Override
    public void submitWorkerState(String address, String dataVersion, WorkerTaskStateBO workerTaskStateBO) {
        boolean putFlag = TaskAssignContext.putIfAbsentTaskState(address, workerTaskStateBO);
        if (putFlag) {
            log.info("接收到 address:{}:{} 上报加载状态请求! {}", address, workerTaskStateBO.getWorkerState(), workerTaskStateBO);
            // 如果首次上报带有分配结果处理信息, 则需要处理
            if (MapUtils.isEmpty(workerTaskStateBO.getAssignChangeMap())) {
                return;
            }
        }
        log.info("接收到 address:{}:{} 上报加载状态请求! {}", address, workerTaskStateBO.getWorkerState(), workerTaskStateBO);
        if (WorkerLoadStateEnum.READY.name().equals(workerTaskStateBO.getWorkerState()) || MapUtils.isEmpty(workerTaskStateBO.getAssignChangeMap())) {
            log.info("加载状态上报 address:{}:{} 处理完成!", address, workerTaskStateBO.getWorkerState());
            return;
        }
        //遍历本次受影响的任务, 将taskItem状态信息更新到数据库
        Map<String, TaskItemEntity> assignChangeMap = workerTaskStateBO.getAssignChangeMap();
        for (Map.Entry<String, TaskItemEntity> entry : assignChangeMap.entrySet()) {
            TaskItemEntity taskItemEntity = entry.getValue();
            if (TaskItemStateEnum.isStop(taskItemEntity.getState())) {
                taskItemService.deleteByTaskItemInfo(taskItemEntity);
            } else {
                taskItemService.update(taskItemEntity);
            }
        }
        if (WorkerLoadStateEnum.FINISH.name().equals(workerTaskStateBO.getWorkerState())) {
            WorkerTaskStateBO cacheWorkerTaskStateBO = TaskAssignContext.getTaskState(address, dataVersion);
            cacheWorkerTaskStateBO.setAssignChangeMap(new HashMap<>());
            cacheWorkerTaskStateBO.setTaskAssignResultMap(workerTaskStateBO.getTaskAssignResultMap());
            cacheWorkerTaskStateBO.setWorkerState(WorkerLoadStateEnum.READY.name());
            log.info("加载状态上报 address:{}:{} 处理完成! {}  cacheWorkerTaskStateBO:{}", address, workerTaskStateBO.getWorkerState(), workerTaskStateBO, cacheWorkerTaskStateBO);
        } else {
            log.info("加载状态上报 address:{}:{} 处理完成! {}", address, workerTaskStateBO.getWorkerState(), workerTaskStateBO);
        }
    }
}
