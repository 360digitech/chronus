package com.qihoo.finance.chronus.executor.bo;

import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by xiongpu on 2019/8/26.
 */
@Slf4j
@Getter
@Setter
public class TaskHeartBeatRunnable implements Runnable {
    public TaskHeartBeatRunnable(Map<String, TaskRuntimeEntity> taskMap, ConcurrentLinkedQueue<TaskRuntimeEntity> concurrentLinkedQueue) {
        this.taskMap = taskMap;
        this.concurrentLinkedQueue = concurrentLinkedQueue;
    }

    private ConcurrentLinkedQueue<TaskRuntimeEntity> concurrentLinkedQueue;
    private Map<String, TaskRuntimeEntity> taskMap;

    @Override
    public void run() {
        ServiceContext serviceContext = ServiceContext.getContext();
        serviceContext.setRequestNo(ServiceContext.genUniqueId());
        ContextLog4j2Util.addContext2ThreadContext();
        String initState = ScheduleServerStatusEnum.init.toString();
        try {
            Set<Map.Entry<String, TaskRuntimeEntity>> entrySet = new HashSet<>(taskMap.entrySet());
            for (Map.Entry<String, TaskRuntimeEntity> entry : entrySet) {
                TaskRuntimeEntity taskRuntime = BeanUtils.copyBean(TaskRuntimeEntity.class, entry.getValue());
                if (ScheduleServerStatusEnum.dead.toString().equals(taskRuntime.getState())) {
                    continue;
                }
                taskRuntime.setHeartBeatTime(DateUtils.now());
                concurrentLinkedQueue.add(taskRuntime);
                if (initState.equals(entry.getValue().getState())) {
                    entry.getValue().setState(ScheduleServerStatusEnum.normal.toString());
                }
            }
        } catch (Exception e) {
            log.error("将心跳数据放入队列异常", e);
        }
    }
}
