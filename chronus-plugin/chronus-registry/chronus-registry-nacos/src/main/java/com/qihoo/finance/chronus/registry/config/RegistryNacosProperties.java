package com.qihoo.finance.chronus.registry.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chronus.registry.nacos")
public class RegistryNacosProperties {

    private String address;

}
