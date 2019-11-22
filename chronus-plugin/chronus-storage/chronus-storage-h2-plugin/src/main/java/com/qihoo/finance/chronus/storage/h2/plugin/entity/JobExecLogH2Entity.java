package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
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
@Table(name = TableConstant.JOB_EXEC_LOG)
public class JobExecLogH2Entity extends BaseH2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * 所属集群
     */
    @NotBlank
    @Column(name = "cluster")
    private String cluster;
    /**
     * 所属系统
     */
    @Column(name = "sys_code")
    private String sysCode;
    /**
     * 任务名称
     */
    @Column(name = "task_name")
    private String taskName;
    /**
     * 执行地址
     */
    @NotBlank
    @Column(name = "exec_address")
    private String execAddress;
    /**
     * 流水号
     */
    @Column(name = "req_no")
    private String reqNo;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;
    /**
     * 处理总数
     */
    @Column(name = "handle_total_count")
    private Long handleTotalCount;
    /**
     * 失败数
     */
    @Column(name = "handle_fail_count")
    private Long handleFailCount;
    /**
     * 处理结果
     */
    @Column(name = "handle_detail")
    private String handleDetail;
    /**
     * 非db字段
     */
    @Transient
    private Integer pageSize;

    @Transient
    private Integer pageNum;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";
}
