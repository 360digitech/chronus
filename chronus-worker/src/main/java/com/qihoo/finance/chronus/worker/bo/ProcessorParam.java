package com.qihoo.finance.chronus.worker.bo;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import com.qihoo.finance.chronus.worker.service.JobDispatcher;
import lombok.Builder;
import lombok.Getter;

/**
 * Created by xiongpu on 2019/8/6.
 */
@Getter
@Builder
public class ProcessorParam {
    /**
     * 任务信息
     */
    private TaskEntity taskEntity;
    /**
     * 任务处理bean
     */
    private JobDispatcher jobDispatcher;

    private TaskItemEntity taskItemEntity;
    /**
     * 任务运行信息
     */
    private volatile TaskRuntimeEntity taskRuntime;

    /**
     * 是否已经获得终止调度信号
     * 用户停止队列调度
     */
    private volatile boolean isStopSchedule;
    /**
     * 调度周期结束,结束则可以继续运行
     */
    private volatile boolean isExecutorEnd;

    public boolean isStopSchedule() {
        return isStopSchedule;
    }

    public void setStopSchedule(boolean stopSchedule) {
        isStopSchedule = stopSchedule;
    }

    public boolean isExecutorEnd() {
        return isExecutorEnd;
    }

    public void setExecutorEnd(boolean executorEnd) {
        isExecutorEnd = executorEnd;
    }
}
