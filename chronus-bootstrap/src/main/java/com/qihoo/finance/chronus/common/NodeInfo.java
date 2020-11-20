package com.qihoo.finance.chronus.common;

import com.qihoo.finance.chronus.common.domain.Domain;
import lombok.Getter;
import lombok.Setter;

/**
 * 当前节点环境信息
 * 运行过程中不会发生变化
 * Created by xiongpu on 2019/9/21.
 */
@Getter
@Setter
public class NodeInfo extends Domain {
    /**
     * 所属集群
     */
    private String cluster;
    /**
     * 所属Tag(分组,用来任务隔离)
     */
    private String tag;
    /**
     * 本机IP
     */
    private String ip;
    private int port;
    /**
     * ip:port
     */
    private String address;
    private String hostName;
    /**
     * 启动版本,每次启动都会生成一个版本号 当前时间的时间戳
     */
    private String version;
    /**
     * 是否为Master
     */
    private String enableMaster;

    /**
     * 是否为执行机
     */
    private String enableWorker;
}
