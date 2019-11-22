package com.qihoo.finance.chronus.metadata.api.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import com.qihoo.finance.chronus.metadata.api.task.enums.ScheduleServerStatusEnum;
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

    public TaskRuntimeEntity(TaskEntity taskEntity) {
        this.cluster = taskEntity.getCluster();
        this.taskName = taskEntity.getTaskName();
        this.registerTime = new Date();
        this.heartBeatTime = new Date();
        this.state = ScheduleServerStatusEnum.init.toString();
    }

    private String cluster;
    private String taskName;
    private Integer seqNo;

    /**
     * 机器IP地址
     */
    private String address;

    /**
     * 机器名称
     */
    private String hostName;

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
     * 最后一次取数据时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date lastFetchDataTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextRunStartTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date nextRunEndTime;

    private String taskItems;

    private String state;

    private String message;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;
}
