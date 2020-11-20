package com.qihoo.finance.chronus.metadata.api.assign.bo;

import com.qihoo.finance.chronus.metadata.api.assign.enums.WorkerLoadStateEnum;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by xiongpu on 2019/10/27.
 */
@Getter
@Setter
public class WorkerTaskStateBO {
    private String tag;

    /**
     * 执行机数据更新状态
     */
    private String workerState;

    private String dataVersion;

    private Map<String, TaskItemEntity> taskAssignResultMap;
    private Map<String, TaskItemEntity> assignChangeMap;

    public WorkerTaskStateBO init(String dataVersion, String tag) {
        this.setWorkerState(WorkerLoadStateEnum.READY.name());
        this.setDataVersion(dataVersion);
        this.setTaskAssignResultMap(new HashMap<>());
        this.setAssignChangeMap(new HashMap<>());
        this.setTag(tag);
        return this;
    }

    public WorkerTaskStateBO init() {
        if (this.taskAssignResultMap == null) {
            this.setTaskAssignResultMap(new HashMap<>());
            this.setAssignChangeMap(new HashMap<>());
        }
        return this;
    }

    public void reset() {
        this.setWorkerState(WorkerLoadStateEnum.RESET.name());
    }

    public void put2StartTaskMap(String key, TaskItemEntity taskItemEntity) {
        this.taskAssignResultMap.put(key, taskItemEntity);
        this.assignChangeMap.put(key, taskItemEntity);
    }

    public void put2ReStartTaskMap(String key, TaskItemEntity taskItemEntity) {
        this.taskAssignResultMap.put(key, taskItemEntity);
        this.assignChangeMap.put(key, taskItemEntity);
    }

    public void put2DestroyTaskMap(String key, TaskItemEntity taskItemEntity) {
        this.taskAssignResultMap.put(key, taskItemEntity);
        this.assignChangeMap.put(key, taskItemEntity);
    }

    @Override
    public String toString() {
        return "WorkerTaskStateBO{" +
                "tag='" + tag + '\'' +
                ", workerState='" + workerState + '\'' +
                ", dataVersion='" + dataVersion + '\'' +
                ", taskAssignResultMap=" + taskAssignResultMap +
                ", assignChangeMap=" + assignChangeMap +
                '}';
    }
}
