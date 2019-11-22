package com.qihoo.finance.chronus.metadata.api.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 调度任务类型
 *
 * @author xiongpu
 */
@Getter
@Setter
public class TaskEntity extends Entity {

    @Id
    private String id;

    @NotBlank
    private String cluster;

    @NotBlank
    private String tag;

    /**
     * 任务名称
     */
    @NotBlank
    private String taskName;

    /**
     * 备注
     */
    private String remark;

    /**
     * 向配置中心更新心跳信息的频率
     * 默认5秒
     */
    private Integer heartBeatRate = 5;

    /**
     * 判断一个服务器死亡的周期。为了安全，至少是心跳周期的两倍以上
     * 默认一分钟
     */
    private Integer judgeDeadInterval = 60;

    /**
     * 当没有数据的时候，休眠的时间
     */
    private Double sleepTimeNoData = 5D;

    /**
     * 在每次数据处理完后休眠的时间
     */
    private Double sleepTimeInterval = 5D;

    /**
     * 每次获取数据的数量
     */
    private Integer fetchDataNumber = 500;

    /**
     * 在批处理的时候，每次处理的数据量
     */
    private Integer executeNumber = 1;

    /**
     * 分发处理线程数量
     */
    private Integer threadNumber = 3;

    /**
     * 调度器类型
     */
    private String processorType = "SLEEP";
    /**
     * 允许执行的开始时间
     */
    @NotBlank
    private String permitRunStartTime;
    /**
     * 允许执行的结束时间
     */
    private String permitRunEndTime;

    /**
     * 处理任务的BeanName CHRONUS_JOB等
     */
    @NotBlank
    private String dealBeanName;
    /**
     * 处理任务的系统编码
     */
    @NotBlank
    private String dealSysCode;

    /**
     * 处理调度逻辑的业务系统beanName
     */
    private String dealBizBeanName;

    /**
     * 任务bean的参数，由用户自定义格式的字符串
     */
    private String taskParameter;

    /**
     * 任务项数组格式化字符串
     */
    private String taskItems;

    /**
     * 强制按照Cron表达式执行
     */
    private String forceCronExec;

    /**
     * 指定需要执行调度的机器数量
     */
    private Integer assignNum = 1;

    private String state = "normal";

    private String isBroadcastInvoker;

    private String executorConfig;


    /* 非db字段 */
    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";

}

