package com.qihoo.finance.chronus.registry.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiongpu on 2019/9/22.
 */
@Getter
@Setter
public class Node {
    private String registerTime;

    private String hostName;

    private String address;

    private String cluster;

    private String tag;

    private String state;

    private String version;
    /**
     * 数据版本, 每次节点上线都会发生变化,初始化是节点的版本(节点启动创建时间)
     */
    private String dataVersion;

    private String isMaster;

    private String enableMaster;
    private String enableWorker;
}
