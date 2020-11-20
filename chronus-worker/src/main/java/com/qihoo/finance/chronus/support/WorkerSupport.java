
package com.qihoo.finance.chronus.support;

import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.context.ServiceContextHelper;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.api.Node;
import com.qihoo.finance.chronus.worker.config.WorkerProperties;
import com.qihoo.finance.chronus.worker.service.WorkerReceiveProcess;
import com.qihoo.finance.chronus.worker.service.WorkerService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/8/2.
 */
@Slf4j
public class WorkerSupport implements Support {
    private static final ScheduledExecutorService LOAD_TASK_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_WORKER, SupportConstants.MAIN));
    private static ScheduledFuture LOAD_TASK_SCHEDULE_FUTURE;
    @Resource
    private NamingService namingService;
    @Resource
    private MasterElectionService masterElectionService;
    @Resource
    private WorkerService workerService;
    @Resource
    private WorkerProperties workerProperties;

    private WorkerTaskStateBO workerTaskStateBO;

    @Override
    public void start() throws Exception {
        Node node = namingService.getCurrentNode();
        WorkerTaskStateBO newWorkerTaskStateBO = new WorkerTaskStateBO().init(node.getDataVersion(), node.getTag());
        if (workerTaskStateBO != null) {
            newWorkerTaskStateBO.setAssignChangeMap(workerTaskStateBO.getAssignChangeMap());
            newWorkerTaskStateBO.setTaskAssignResultMap(workerTaskStateBO.getTaskAssignResultMap());
        }
        workerTaskStateBO = newWorkerTaskStateBO;
        if (LOAD_TASK_SCHEDULE_FUTURE != null && !LOAD_TASK_SCHEDULE_FUTURE.isCancelled()) {
            LOAD_TASK_SCHEDULE_FUTURE.cancel(false);
        }
        LOAD_TASK_SCHEDULE_FUTURE = LOAD_TASK_SCHEDULE.scheduleWithFixedDelay(workerLoadTaskTimerTask, workerProperties.getLoadTaskDataTimerTaskInitialDelay(), workerProperties.getLoadTaskDataTimerTaskDelay(), TimeUnit.SECONDS);
    }

    private Runnable workerLoadTaskTimerTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_WORKER, "loadTask", true) {
        @Override
        public void process() throws Exception {
            String masterNodeAddress;
            while (true) {
                ServiceContextHelper.initContext();
                masterNodeAddress = masterElectionService.getCurrentMasterNodeAddress();
                if (masterNodeAddress == null) {
                    log.warn("Master 节点心跳已经失效, 跳过本次数据加载!");
                    return;
                }
                Node node = namingService.getCurrentNode();
                if (node == null) {
                    log.warn("当前节点已离线, 停止所有任务加载!");
                    return;
                }
                WorkerTaskStateBO tmpWorkerTaskStateBO = SpringContextHolder.getBean(WorkerReceiveProcess.class).init(node, workerTaskStateBO, masterNodeAddress)
                        .submitState()
                        .getAssignResult()
                        .receiveProcess()
                        .finish()
                        .getWorkerTaskStateBO();
                if (tmpWorkerTaskStateBO == null) {
                    return;
                }
                workerTaskStateBO = tmpWorkerTaskStateBO;
            }
        }
    };

    @Override
    public void stop() {
        if (LOAD_TASK_SCHEDULE_FUTURE != null && !LOAD_TASK_SCHEDULE_FUTURE.isCancelled()) {
            LOAD_TASK_SCHEDULE_FUTURE.cancel(false);
        }
        workerService.destroyAllTask();
    }
}
