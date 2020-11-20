package com.qihoo.finance.chronus.registry.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chronus.registry.zookeeper")
public class RegistryZookeeperProperties {

    private String address;
    private String rootPath = "/chronus";
    private String userName;
    private String password;
    private int sessionTimeout = 60000;
    private int connectionTimeout = 60000;
}
