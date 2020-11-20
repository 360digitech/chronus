package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:28:19
 * @version 5.1.0
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.TASK_INFO)
public class TaskH2Entity extends BaseH2Entity{
	private static final long serialVersionUID = 1L;

	@Id
    private String id;

    @Column(name = "cluster")
    private String cluster;

    @Column(name = "tag")
    private String tag;

    /**
     * 任务名称
     */
    @Column(name = "task_name")
    private String taskName;

    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

    /**
     * 向配置中心更新心跳信息的频率
     * 默认5秒
     */
    @Column(name = "heart_beat_rate")
    private Integer heartBeatRate = 5;

    /**
     * 判断一个服务器死亡的周期。为了安全，至少是心跳周期的两倍以上
     * 默认一分钟
     */
    @Column(name = "judge_dead_interval")
    private Integer judgeDeadInterval = 60;

    /**
     * 当没有数据的时候，休眠的时间
     */
    @Column(name = "sleep_time_no_data")
    private Long sleepTimeNoData = 5L;

    /**
     * 在每次数据处理晚后休眠的时间
     */
    @Column(name = "sleep_time_interval")
    private Long sleepTimeInterval = 5L;

    /**
     * 每次获取数据的数量
     */
    @Column(name = "fetch_data_number")
    private Integer fetchDataNumber = 500;

    /**
     * 在批处理的时候，每次处理的数据量
     */
    @Column(name = "execute_number")
    private Integer executeNumber = 1;

    /**
     * 分发处理线程数量
     */
    @Column(name = "thread_number")
    private Integer threadNumber = 3;

    /**
     * 调度器类型
     */
    @Column(name = "processor_type")
    private String processorType = "SLEEP";
    /**
     * 允许执行的开始时间
     */
    @Column(name = "permit_run_start_time")
    private String permitRunStartTime;
    /**
     * 允许执行的结束时间
     */
    @Column(name = "permit_run_end_time")
    private String permitRunEndTime;

    /**
     * 处理任务的BeanName CHRONUS_JOB等
     */
    @Column(name = "deal_bean_name")
    private String dealBeanName;
    /**
     * 处理任务的系统编码
     */
    @Column(name = "deal_sys_code")
    private String dealSysCode;

    /**
     * 处理调度逻辑的业务系统beanName
     */
    @Column(name = "deal_biz_bean_name")
    private String dealBizBeanName;

    /**
     * 任务bean的参数，由用户自定义格式的字符串
     */
    @Column(name = "task_parameter")
    private String taskParameter;

    /**
     * 任务项数组格式化字符串
     */
    @Column(name = "task_items")
    private String taskItems;

    /**
     * 强制按照Cron表达式执行
     */
    @Column(name = "force_cron_exec")
    private String forceCronExec;

    /**
     * 指定需要执行调度的机器数量
     */
    @Column(name = "assign_num")
    private Integer assignNum = 1;

    @Column(name = "state")
    private String state = "normal";

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
