package com.qihoo.finance.chronus.metadata.api.log.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 调度执行日志
 *
 * @author chenxiyu
 */
@Getter
@Setter
public class JobExecLogEntity extends Entity {

    @Id
    private String id;

    /**
     * 所属集群
     */
    @NotBlank
    private String cluster;
    /**
     * 所属系统
     */
    private String sysCode;
    /**
     * 任务名称
     */
    private String taskName;
    /**
     * 执行地址
     */
    @NotBlank
    private String execAddress;
    /**
     * 流水号
     */
    private String reqNo;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date startDate;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date endDate;
    /**
     * 处理总数
     */
    private Long handleTotalCount;
    /**
     * 失败数
     */
    private Long handleFailCount;
    /**
     * 处理结果
     */
    private String handleDetail;

    /* 非db字段 */

    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;
}

