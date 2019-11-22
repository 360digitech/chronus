package com.qihoo.finance.chronus.common;

import com.qihoo.finance.chronus.common.domain.Domain;
import lombok.Getter;
import lombok.Setter;

/**
 * 当前节点环境信息
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
     * 启动版本
     */
    private String version;
    /**
     * 是否为Master
     */
    private String enableMaster;

    /**
     * 是否为执行机
     */
    private String enableExecutor;
}
