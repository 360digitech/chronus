package com.qihoo.finance.chronus.support;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.master.bo.TaskAssignContext;
import com.qihoo.finance.chronus.master.config.MasterProperties;
import com.qihoo.finance.chronus.master.service.TaskAssignRefreshService;
import com.qihoo.finance.chronus.master.service.TaskAssignService;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/7/28.
 */
@Slf4j
public class MasterSupport implements Support {
    private static final ScheduledExecutorService TASK_ASSIGN_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_MASTER, SupportConstants.MAIN));
    private static ScheduledFuture TASK_ASSIGN_SCHEDULE_FUTURE;
    @Resource
    private MasterElectionService masterElectionService;

    @Resource
    private MasterProperties masterProperties;

    @Resource
    private TaskAssignRefreshService taskAssignRefreshService;

    @Resource
    private NodeInfo currentNode;

    @Override
    public void start() throws Exception {
        if (TASK_ASSIGN_SCHEDULE_FUTURE != null && !TASK_ASSIGN_SCHEDULE_FUTURE.isCancelled()) {
            TASK_ASSIGN_SCHEDULE_FUTURE.cancel(false);
        }
        TASK_ASSIGN_SCHEDULE_FUTURE = TASK_ASSIGN_SCHEDULE.scheduleWithFixedDelay(masterTimerTask, masterProperties.getTaskAssignTimerTaskInitialDelay(), masterProperties.getTaskAssignTimerTaskDelay(), TimeUnit.SECONDS);
    }

    private Runnable masterTimerTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_MASTER, "taskAssign", true) {
        @Override
        public void process() throws Exception {
            masterElectionService.setMasterGroupByTag(masterProperties.isMasterGroupByTag());
            if (masterElectionService.isMaster()) {
                TaskAssignService taskAssignService = TaskAssignService.create().init();
                if (taskAssignService.isNeedAssign()) {
                    taskAssignService.taskAssign();
                }
                return;
            }
            log.debug("当前节点非Master,开始对现有Master进行检查...");
            if (masterElectionService.isActiveMaster()) {
                taskAssignRefreshService.shutdownRefreshTask();
                return;
            }
            String masterNodeAddress = masterElectionService.election(currentNode.getAddress());
            log.info("节点被选举为Master IP:{}", masterNodeAddress);
            if (Objects.equals(currentNode.getAddress(), masterNodeAddress)) {
                TaskAssignContext.clear();
                taskAssignRefreshService.restartRefreshTask();
            }
        }
    };

    @Override
    public void stop() {
        if (TASK_ASSIGN_SCHEDULE_FUTURE != null && !TASK_ASSIGN_SCHEDULE_FUTURE.isCancelled()) {
            TASK_ASSIGN_SCHEDULE_FUTURE.cancel(false);
        }

        taskAssignRefreshService.shutdownRefreshTask();
    }
}
