package com.qihoo.finance.chronus.metadata.api.task.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.validator.constraints.Length;
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
@Accessors(chain = true)
public class TaskEntity extends Entity {

    @Id
    private String id;

    /**
     * 任务优先执行集群
     */
    @NotBlank(message = "优先运行集群必选!")
    private String cluster;

    @NotBlank(message = "任务归属TAG必选!")
    private String tag;

    /**
     * 任务名称
     */
    @Length(min = 3, max = 64, message = "请使用合理的任务名称!")
    private String taskName;

    /**
     * 备注
     */
    @Length(max = 64, message = "任务备注不能过长!")
    private String remark;

    /**
     * 向配置中心更新心跳信息的频率
     * 默认5秒
     */
    private Integer heartBeatRate = 5;

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
    @Length(min = 1, max = 32, message = "调度器类型必选!")
    private String processorType = "SelectExecuteFlow";
    /**
     * 允许执行的开始时间
     */
    @NotBlank(message = "任务启动时间必填!")
    private String permitRunStartTime;

    /**
     * 处理任务的系统编码
     */
    @NotBlank(message = "任务归属服务必选!")
    private String dealSysCode;

    /**
     * 处理调度逻辑的业务系统beanName
     */
    @NotBlank(message = "业务系统任务处理Bean必填!")
    private String dealBizBeanName;

    /**
     * 任务bean的参数，由用户自定义格式的字符串
     */
    @Length(max = 5000, message = "任务处理参数不能过长!")
    private String taskParameter;

    /**
     * 任务项数组格式化字符串
     */
    @Length(max = 2200, message = "任务项参数不能过长!")
    private String taskItems;

    /**
     * 指定需要执行调度的机器数量
     */
    private Integer assignNum = 1;

    private String state = "START";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";

    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;
}

