package com.qihoo.finance.chronus.metadata.api.assign.bo;

import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadPhaseEnum;
import com.qihoo.finance.chronus.metadata.api.assign.enums.ExecutorLoadStateEnum;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongpu on 2019/10/27.
 */
@Getter
@Setter
public class ExecutorTaskStateBO {
    private String tag;

    /**
     * 执行机数据更新状态
     */
    private String executorState;

    /**
     * 执行机数据更新阶段
     */
    private Integer executorPhase;

    private String executorVersion;

    private String dataVersion;


    private List<TaskAssignResultEntity> taskAssignResultList;

    private List<TaskAssignResultEntity> needStartTaskList;

    private List<TaskAssignResultEntity> needStopTaskList;

    public void init(String executorVersion, String dataVersion) {
        this.setExecutorPhase(ExecutorLoadPhaseEnum.INIT.getPhase());
        this.setExecutorVersion(executorVersion);
        this.setDataVersion(dataVersion);
        this.setTaskAssignResultList(new ArrayList<>());
    }

    public boolean removeSuccess() {
        return ExecutorLoadPhaseEnum.REMOVE.isEquals(this.getExecutorPhase()) && ExecutorLoadStateEnum.SUCC.isEquals(this.getExecutorState());
    }

    public boolean addSuccess() {
        return ExecutorLoadPhaseEnum.ADD.isEquals(this.getExecutorPhase()) && ExecutorLoadStateEnum.SUCC.isEquals(this.getExecutorState());
    }

    public boolean offlineSuccess() {
        return ExecutorLoadPhaseEnum.OFFLINE.isEquals(this.getExecutorPhase()) && ExecutorLoadStateEnum.SUCC.isEquals(this.getExecutorState());
    }

    public void offline(List<TaskAssignResultEntity> needStopTaskList) {
        this.setNeedStopTaskList(needStopTaskList);
        this.setNeedStartTaskList(new ArrayList<>());
        this.setExecutorPhase(ExecutorLoadPhaseEnum.OFFLINE.getPhase());
        this.setTaskAssignResultList(new ArrayList<>());
    }

    @Override
    public String toString() {
        return "ExecutorTaskStateBO{" +
                "tag='" + tag + '\'' +
                ", executorState='" + executorState + '\'' +
                ", executorPhase=" + executorPhase +
                ", executorVersion='" + executorVersion + '\'' +
                ", dataVersion='" + dataVersion + '\'' +
                ", taskAssignResultList=" + taskAssignResultList +
                ", needStartTaskList=" + needStartTaskList +
                ", needStopTaskList=" + needStopTaskList +
                '}';
    }
}
