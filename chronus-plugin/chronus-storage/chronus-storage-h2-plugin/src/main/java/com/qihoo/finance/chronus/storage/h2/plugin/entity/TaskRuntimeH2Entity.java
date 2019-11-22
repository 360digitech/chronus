package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Date;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.TASK_RUNTIME_INFO)
public class TaskRuntimeH2Entity extends BaseH2Entity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "cluster")
    private String cluster;

    @Column(name = "task_name")
    private String taskName;

    /**
     * 机器IP地址
     */
    @Column(name = "address")
    private String address;

    /**
     * 机器名称
     */
    @Column(name = "host_name")
    private String hostName;

    /**
     * 服务开始时间
     */
    @Column(name = "register_time")
    private Date registerTime;
    /**
     * 最后一次心跳通知时间
     */
    @Column(name = "heart_beat_time")
    private Date heartBeatTime;
    /**
     * 最后一次取数据时间
     */
    @Column(name = "last_fetch_data_time")
    private Date lastFetchDataTime;

    @Column(name = "next_run_start_time")
    private Date nextRunStartTime;

    @Column(name = "next_run_end_time")
    private Date nextRunEndTime;

    /**
     * 数据版本号
     */
    @Column(name = "version")
    private String version;

    @Column(name = "task_items")
    private String taskItems;

    @Column(name = "state")
    private String state;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";

}
