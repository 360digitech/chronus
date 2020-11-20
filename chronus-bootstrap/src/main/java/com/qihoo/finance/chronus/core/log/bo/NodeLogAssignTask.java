package com.qihoo.finance.chronus.core.log.bo;

import com.qihoo.finance.chronus.common.domain.Domain;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 节点日志-任务分配信息
 * log: List<NodeLogAssign>
 * com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum#MASTER_TASK_ASSIGN
 */
@Getter
@Setter
public class NodeLogAssignTask extends Domain {

    public NodeLogAssignTask() {
    }

    public NodeLogAssignTask(TaskItemEntity taskItemEntity, String newExecAddress) {
        this.taskItemId = taskItemEntity.getTaskItemId();
        this.state = taskItemEntity.getState();
        this.oldExecAddress = taskItemEntity.getWorkerAddress();
        this.newExecAddress = newExecAddress;
    }

    /**
     * 任务key
     */
    private String taskItemId;
    private String state;

    /**
     * 旧的执行机地址,可为空
     */
    private String oldExecAddress;
    /**
     * 新的执行机地址,可为空
     */
    private String newExecAddress;
}
