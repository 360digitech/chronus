package com.qihoo.finance.chronus.worker.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chronus.worker")
public class WorkerProperties {

    private boolean enabled = true;


    /**
     * 加载任务数据定时任务
     * 初始化延迟时间 单位秒
     */
    private long loadTaskDataTimerTaskInitialDelay = 15;

    /**
     * 加载任务数据定时任务
     * 固定延迟时间 单位秒
     */
    private long loadTaskDataTimerTaskDelay = 3;
    /**
     * 发送任务心跳信息定时任务
     * 初始化延迟时间 单位秒
     */
    private long sendTaskHeartbeatInfoTimerTaskInitialDelay = 15;

    /**
     * 发送任务心跳信息定时任务
     * 固定延迟时间 单位秒
     */
    private long sendTaskHeartbeatInfoTimerTaskDelay = 2;

    /**
     * 发送任务心跳信息定时任务
     * 每次加载心跳数据最小等待时间 单位秒
     */
    private double loadHeartbeatInfoTime = 1.5;

    /**
     * 保存执行日志
     * 每多少条保存一次
     */
    private long saveJobExecLogTaskBatchNum = 500;

    /**
     * 保存执行日志定时任务
     * 初始化延迟时间 单位秒
     */
    private long saveJobExecLogTaskInitialDelay = 15;

    /**
     * 保存执行日志定时任务
     * 固定延迟时间 单位秒
     */
    private long saveJobExecLogTaskDelay = 1;

    /**
     * 保存执行日志定时任务
     * 每次加载执行日志数据最小等待时间 单位秒
     */
    private double saveJobExecLogTime = 0.5;
}
