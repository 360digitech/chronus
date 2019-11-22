package com.qihoo.finance.chronus.executor.service.impl;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.core.task.service.TaskRuntimeService;
import com.qihoo.finance.chronus.executor.bo.TaskHeartBeatRunnable;
import com.qihoo.finance.chronus.executor.config.ExecutorProperties;
import com.qihoo.finance.chronus.executor.service.TaskHeartBeatService;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.*;

/**
 * Created by xiongpu on 2019/8/26.
 */
@Slf4j
public class TaskHeartBeatServiceImpl implements TaskHeartBeatService, ApplicationListener<ContextRefreshedEvent> {
    /**
     * 所有任务运行信息按心跳间隔分组
     * 心跳周期,taskName#seqNo,taskRuntime
     */
    private static final Table<String, String, TaskRuntimeEntity> TASK_HEARTBEAT_INTERVAL_GROUP_TABLE = HashBasedTable.create();
    /**
     * 不同心跳区间不同的定时任务 并且将数据刷到内存队列
     */
    private static final Map<String, ScheduledExecutorService> HEARTBEAT_INTERVAL_SCHEDULED_MAP = new HashMap<>();
    /**
     * 心跳信息
     */
    private static final Map<String, ConcurrentLinkedQueue<TaskRuntimeEntity>> CONCURRENT_HEARTBEAT_QUEUE_MAP = new HashMap<>();
    /**
     * 将数据从内存队列刷新到线程池
     */
    private static final ScheduledExecutorService SEND_TASK_RUNTIME_HEARTBEAT_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_EXECUTOR, SupportConstants.SEND_TASK_RUNTIME_HEARTBEAT));
    /**
     * 线程池将数据存到数据库
     */
    private static final ExecutorService SAVE_DATA_EXECUTOR_SERVICE = Executors.newFixedThreadPool(20);

    private Interner<String> pool = Interners.newWeakInterner();

    @Resource
    private TaskRuntimeService taskRuntimeService;

    @Resource
    private ExecutorProperties executorProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        SEND_TASK_RUNTIME_HEARTBEAT_SCHEDULE.scheduleWithFixedDelay(sendTaskHeartbeatInfoTask, executorProperties.getSendTaskHeartbeatInfoTimerTaskInitialDelay(), executorProperties.getSendTaskHeartbeatInfoTimerTaskDelay(), TimeUnit.SECONDS);
    }

    /**
     * 将某个心跳间隔的任务运行信息 添加到 心跳服务定时发送队列
     * 不同间隔周期的定时任务 将数据丢到统一的队列
     * 心跳数据更新任务将队列数据更新或者插入到db
     *
     * @param heartBeatRate
     * @param taskRuntime
     */
    @Override
    public void addTaskToHeartBeatQueue(Integer heartBeatRate, TaskRuntimeEntity taskRuntime) {
        String heartBeatRateStr = heartBeatRate.toString();
        String key = getKey(taskRuntime);

        TASK_HEARTBEAT_INTERVAL_GROUP_TABLE.put(heartBeatRateStr, key, taskRuntime);

        if (!HEARTBEAT_INTERVAL_SCHEDULED_MAP.containsKey(heartBeatRateStr)) {
            synchronized (pool.intern(heartBeatRateStr)) {
                ConcurrentLinkedQueue<TaskRuntimeEntity> concurrentLinkedQueue = CONCURRENT_HEARTBEAT_QUEUE_MAP.get(heartBeatRateStr);
                if (concurrentLinkedQueue == null) {
                    concurrentLinkedQueue = CONCURRENT_HEARTBEAT_QUEUE_MAP.computeIfAbsent(heartBeatRateStr, k -> new ConcurrentLinkedQueue<>());
                }

                if (!HEARTBEAT_INTERVAL_SCHEDULED_MAP.containsKey(heartBeatRateStr)) {
                    ScheduledExecutorService heartbeatIntervalScheduledService = Executors.newSingleThreadScheduledExecutor();
                    HEARTBEAT_INTERVAL_SCHEDULED_MAP.put(heartBeatRateStr, heartbeatIntervalScheduledService);
                    // 启动定时任务 刷心跳信息到内存队列里面
                    TaskHeartBeatRunnable taskHeartBeatRunnable = new TaskHeartBeatRunnable(TASK_HEARTBEAT_INTERVAL_GROUP_TABLE.row(heartBeatRateStr), concurrentLinkedQueue);
                    heartbeatIntervalScheduledService.scheduleWithFixedDelay(taskHeartBeatRunnable, 1, heartBeatRate, TimeUnit.SECONDS);
                }
            }
        }
    }

    @Override
    public void removeTaskFromHeartBeatQueue(Integer heartBeatRate, TaskRuntimeEntity taskRuntime) {
        String heartBeatRateStr = heartBeatRate.toString();
        String key = getKey(taskRuntime);

        TASK_HEARTBEAT_INTERVAL_GROUP_TABLE.remove(heartBeatRateStr, key);
        // 如果这个间隔不存在需要发送心跳的任务 则清空相关队列
        Map<String, TaskRuntimeEntity> rowMap = TASK_HEARTBEAT_INTERVAL_GROUP_TABLE.row(heartBeatRateStr);
        if (MapUtils.isEmpty(rowMap)) {
            ScheduledExecutorService heartbeatIntervalScheduledService = HEARTBEAT_INTERVAL_SCHEDULED_MAP.remove(heartBeatRateStr);
            if (heartbeatIntervalScheduledService != null) {
                heartbeatIntervalScheduledService.shutdown();
            }
            log.info("移除{}/s周期的心跳数据队列!", heartBeatRateStr);
            CONCURRENT_HEARTBEAT_QUEUE_MAP.remove(heartBeatRateStr);
        }
        taskRuntimeService.delete(taskRuntime);
    }

    private Runnable sendTaskHeartbeatInfoTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_EXECUTOR, SupportConstants.SEND_TASK_RUNTIME_HEARTBEAT) {
        @Override
        public void process() throws Exception {
            int i = 0;
            Long startTime = System.currentTimeMillis();
            Set<Map.Entry<String, ConcurrentLinkedQueue<TaskRuntimeEntity>>> entrySet = CONCURRENT_HEARTBEAT_QUEUE_MAP.entrySet();
            while (CollectionUtils.isNotEmpty(entrySet) && System.currentTimeMillis() - startTime < executorProperties.getLoadHeartbeatInfoTime() * ChronusConstants.MILLISECOND_2_SECOND) {
                for (Map.Entry<String, ConcurrentLinkedQueue<TaskRuntimeEntity>> entry : entrySet) {
                    if (entry.getValue().isEmpty()) {
                        Thread.sleep(200);
                        continue;
                    }
                    i++;
                    TaskRuntimeEntity e = entry.getValue().poll();
                    if (!ScheduleServerStatusEnum.dead.toString().equals(e.getState())) {
                        SAVE_DATA_EXECUTOR_SERVICE.submit(() -> taskRuntimeService.updateTaskRuntimeHeartBeatTime(e));
                    }
                }
            }
            SAVE_DATA_EXECUTOR_SERVICE.awaitTermination(3, TimeUnit.SECONDS);
            log.info("心跳数据发送完成,total:{}", i);
        }
    };


    private String getKey(TaskRuntimeEntity taskRuntime) {
        String key = taskRuntime.getTaskName() + "#" + taskRuntime.getSeqNo();
        return key;
    }
}
