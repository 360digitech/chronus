package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:27:19
 * @version 5.1.0
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.JOB_EXEC_LOG_INFO)
public class JobExecLogH2Entity extends BaseH2Entity {
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

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
    
    @Column(name = "date_created")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";
    
    /**
     * 非db字段
     */
    @Transient
    private Integer pageSize;

    @Transient
    private Integer pageNum;
}
