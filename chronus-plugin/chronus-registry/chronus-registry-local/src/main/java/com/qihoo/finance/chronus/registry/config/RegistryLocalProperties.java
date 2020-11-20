package com.qihoo.finance.chronus.registry.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Created by xiongpu on 2020/7/26.
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "chronus.registry.local")
public class RegistryLocalProperties {

}
