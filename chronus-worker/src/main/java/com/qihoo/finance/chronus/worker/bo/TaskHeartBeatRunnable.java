package com.qihoo.finance.chronus.worker.bo;

import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.context.ServiceContext;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
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
        try {
            Set<Map.Entry<String, TaskRuntimeEntity>> entrySet = new HashSet<>(taskMap.entrySet());
            for (Map.Entry<String, TaskRuntimeEntity> entry : entrySet) {
                if (TaskRunStatusEnum.DEAD.toString().equals(entry.getValue().getState())) {
                    continue;
                }
                if (entry.getValue().getDateUpdated() != null && entry.getValue().getDateUpdated().compareTo(entry.getValue().getHeartBeatTime()) > 0) {
                    TaskRuntimeEntity taskRuntime = BeanUtils.copyBean(TaskRuntimeEntity.class, entry.getValue());
                    taskRuntime.setHeartBeatTime(DateUtils.now());
                    concurrentLinkedQueue.add(taskRuntime);
                }
            }
        } catch (Throwable e) {
            log.error("将心跳数据放入队列异常", e);
        }
    }
}
