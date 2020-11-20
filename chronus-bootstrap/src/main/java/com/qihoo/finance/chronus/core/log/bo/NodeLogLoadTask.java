package com.qihoo.finance.chronus.core.log.bo;

import com.alibaba.fastjson.annotation.JSONField;
import com.qihoo.finance.chronus.common.domain.Domain;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.Getter;
import lombok.Setter;

/**
 * 节点日志-执行机加载信息
 * log: List<NodeLogLoad>
 * com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum#WORKER_LOAD_TASK
 */
@Getter
@Setter
public class NodeLogLoadTask extends Domain {

    public NodeLogLoadTask() {
    }

    @JSONField(serialize = false)
    private long startTime;


    public NodeLogLoadTask(TaskItemEntity taskItemEntity) {
        this.taskItemId = taskItemEntity.getTaskItemId();
        this.state = taskItemEntity.getState();
        this.message = taskItemEntity.getMessage();
        this.startTime = System.currentTimeMillis();
    }

    /**
     * 任务key
     */
    private String taskItemId;

    /**
     * 加载状态
     */
    private String state;

    private String result;
    /**
     * 其他信息
     * 异常日志
     */
    private String message;

    /**
     * 加载耗时
     */
    private long costTime;

    public void finish(String result, String message) {
        this.result = result;
        this.message = message;
        this.costTime = System.currentTimeMillis() - startTime;
    }
}
