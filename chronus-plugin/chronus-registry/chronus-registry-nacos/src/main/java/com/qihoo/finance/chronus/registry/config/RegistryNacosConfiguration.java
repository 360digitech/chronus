package com.qihoo.finance.chronus.registry.config;

import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingMaintainFactory;
import com.alibaba.nacos.api.naming.NamingMaintainService;
import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.nacos.MasterElectionServiceNacosImpl;
import com.qihoo.finance.chronus.registry.nacos.NamingServiceNacosImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Configuration
@EnableConfigurationProperties({RegistryNacosProperties.class})
@ConditionalOnProperty(prefix = "chronus", name = {"registry"}, havingValue = "nacos")
public class RegistryNacosConfiguration {

    /**
     * 基于Nacos的Master策略选举类
     *
     * @return
     */
    @Bean("masterElectionStrategy#nacos")
    public MasterElectionService masterElectionStrategyDefaultService() {
        MasterElectionService masterElectionStrategyService = new MasterElectionServiceNacosImpl();
        return masterElectionStrategyService;
    }

    @Bean
    public NamingService namingService() {
        NamingService namingService = new NamingServiceNacosImpl();
        return namingService;
    }

    @Bean
    public NamingMaintainService namingMaintainService(RegistryNacosProperties registryNacosProperties, Environment environment) throws NacosException {
        String address = registryNacosProperties.getAddress();
        if (StringUtils.isBlank(address)) {
            address = environment.getProperty("nacos.discovery.server-addr");
        }
        if (StringUtils.isBlank(address)) {
            address = environment.getProperty("dubbo.registry.address");
            if (StringUtils.isNotBlank(address)) {
                //nacos://127.0.0.1:2181?backup=127.0.0.1:2182,127.0.0.1:2183
                address = address.replace("nacos://", "");
                address = address.replace("?backup=", ",");
            }
        }
        NamingMaintainService namingMaintainService = NamingMaintainFactory.createMaintainService(address);
        return namingMaintainService;
    }
}
