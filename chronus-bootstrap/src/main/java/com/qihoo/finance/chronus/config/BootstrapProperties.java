package com.qihoo.finance.chronus.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chronus.bootstrap")
public class BootstrapProperties {
    /**
     * config cluster key default "IDC"
     */
    private String clusterKey = "IDC";

    private String tagKey = "TAG";
}
