package com.qihoo.finance.chronus.metadata.api.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * Created by xiongpu on 2019/8/3.
 */
@Getter
@Setter
public class TaskRuntimeEntity extends Entity {

    @Id
    private String id;

    public TaskRuntimeEntity() {
    }

    public TaskRuntimeEntity(TaskItemEntity taskItemEntity) {
        this.taskItemId = taskItemEntity.getTaskItemId();
        this.registerTime = new Date();
        this.heartBeatTime = new Date();
        this.dateCreated = new Date();
        this.dateUpdated = new Date();
        this.state = TaskRunStatusEnum.INIT.toString();
        this.message = "初始化成功，等待Master分配！";
    }

    private String taskItemId;

    /**
     * 服务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;
    /**
     * 最后一次心跳通知时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date heartBeatTime;
    /**
     * 最后一次运行时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastRunDataTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextRunStartTime;

    /**
     * 运行状态
     */
    private String state;

    private String message;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    public void createdSuccess() {
        this.state = TaskRunStatusEnum.INIT.name();
        this.message = "初始化成功!";
    }
}
