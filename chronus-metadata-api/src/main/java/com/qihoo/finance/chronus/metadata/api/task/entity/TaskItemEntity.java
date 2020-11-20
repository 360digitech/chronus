package com.qihoo.finance.chronus.metadata.api.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.assign.enums.TaskItemStateEnum;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import com.qihoo.finance.chronus.metadata.api.task.enums.TaskRunStatusEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * 任务项信息
 * Created by xiongpu on 2019/8/3.
 */
@Getter
@Setter
public class TaskItemEntity extends Entity {

    @Id
    private String id;

    public TaskItemEntity() {
    }

    public TaskItemEntity(TaskEntity taskEntity, Integer seqNo) {
        this.tag = taskEntity.getTag();
        this.dealSysCode = taskEntity.getDealSysCode();
        this.taskName = taskEntity.getTaskName();
        this.seqNo = seqNo;
        this.taskItemId = String.join("#", this.dealSysCode, this.taskName, String.valueOf(this.seqNo));

        this.registerTime = new Date();
        this.state = TaskRunStatusEnum.INIT.toString();
        this.taskDateUpdated = taskEntity.getDateUpdated();
        this.message = "初始化成功，等待Master分配！";
        this.version = 0;
    }

    private String taskItemId;
    private String cluster;
    private String tag;
    private String dealSysCode;
    private String taskName;
    private Integer seqNo;


    private String masterAddress;
    private String masterVersion;
    /**
     * 执行机器IP地址
     */
    private String workerAddress;
    /**
     * 执行机版本 dataVersion
     */
    private String workerVersion;

    /**
     * 服务开始时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date registerTime;

    private String taskItems;
    /**
     * 加载状态
     */
    private String state;
    /**
     * 加载信息
     */
    private String message;
    /**
     * 运行信息版本
     * 创建时会校验版本
     */
    private long version;

    /**
     * 对应任务的最后一次更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date taskDateUpdated;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    public void init(String workerAddress, String workerVersion) {
        this.workerAddress = workerAddress;
        this.workerVersion = workerVersion;
    }

    public void createdSuccess() {
        this.state = TaskItemStateEnum.START.name();
        this.message = "初始化成功!";
    }

    public void createdFail(String message) {
        this.state = TaskItemStateEnum.ERROR.name();
        this.message = message;
    }
}
