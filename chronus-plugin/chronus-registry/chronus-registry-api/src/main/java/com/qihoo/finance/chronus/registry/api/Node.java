package com.qihoo.finance.chronus.registry.api;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiongpu on 2019/9/22.
 */
@Getter
@Setter
public class Node {
    private String hostName;

    private String address;

    private String cluster;

    private String tag;

    private String state;

    private String version;
    private String dataVersion;

    private String isMaster;

    private String enableMaster;
    private String enableExecutor;
}
