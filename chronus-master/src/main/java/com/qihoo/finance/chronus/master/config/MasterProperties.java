package com.qihoo.finance.chronus.master.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "chronus.master")
public class MasterProperties {

    /**
     * 是否启动Master相关功能
     */
    private boolean enabled = true;

    /**
     * 任务分配定时任务
     * 初始化延迟时间 单位秒
     */
    private long taskAssignTimerTaskInitialDelay = 3;

    /**
     * 任务分配定时任务
     * 固定延迟时间 单位秒
     */
    private long taskAssignTimerTaskDelay = 5;

    /**
     * 按tag分组 ，每个tag 一个master
     * 如果是true 需要每个tag组所在的节点集群有开启master角色的节点
     */
    private boolean masterGroupByTag = false;
}
