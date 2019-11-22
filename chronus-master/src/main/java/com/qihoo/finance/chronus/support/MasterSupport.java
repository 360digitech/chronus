package com.qihoo.finance.chronus.support;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.master.config.MasterProperties;
import com.qihoo.finance.chronus.master.service.TaskAssignService;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.NamingService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/7/28.
 */
@Slf4j
public class MasterSupport implements Support {
    private static final ScheduledExecutorService TASK_ASSIGN_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_MASTER, SupportConstants.MAIN));
    @Resource
    private MasterElectionService masterElectionService;

    @Resource
    private MasterProperties masterProperties;

    @Resource
    private TaskAssignService taskAssignService;

    @Resource
    private NamingService namingService;

    @Resource
    private NodeInfo currentNode;

    @Override
    public void start() throws Exception {
        TASK_ASSIGN_SCHEDULE.scheduleWithFixedDelay(masterTimerTask, masterProperties.getTaskAssignTimerTaskInitialDelay(), masterProperties.getTaskAssignTimerTaskDelay(), TimeUnit.SECONDS);
    }

    private Runnable masterTimerTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_MASTER, "taskAssign") {
        @Override
        public void process() throws Exception {
            if (!namingService.currentNodeIsActive()) {
                log.warn("当前节点已下线,停止处理!");
                taskAssignService.clear();
                return;
            }
            if (namingService.isMaster()) {
                taskAssignService.taskAssign();
                return;
            }

            if (namingService.isActiveMaster()) {
                return;
            }

            String masterNodeAddress = masterElectionService.election();
            if (Objects.equals(currentNode.getAddress(), masterNodeAddress)) {
                log.info("当前节点自动被选举为Master IP:{}", masterNodeAddress);
                namingService.nodeElectedMaster(masterNodeAddress);
            }
        }
    };

    @Override
    public void stop() {
        TASK_ASSIGN_SCHEDULE.shutdown();
    }
}
