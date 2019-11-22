package com.qihoo.finance.chronus;

import com.qihoo.finance.chronus.sdk.AbstractSdkService;
import com.qihoo.finance.chronus.sdk.ChronusSdkProcessor;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.apache.dubbo.config.ServiceConfig;
import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChronusAutoConfiguration
 *
 * @author nisiyong
 * @date 2019/11/04
 */
@Configuration
@AutoConfigureAfter(DubboAutoConfiguration.class)
public class ChronusAutoConfiguration {

    private final ApplicationConfig applicationConfig;
    private final RegistryConfig registryConfig;

    public ChronusAutoConfiguration(ObjectProvider<ApplicationConfig> applicationConfig,
                                    ObjectProvider<RegistryConfig> registryConfig) {
        this.applicationConfig = applicationConfig.getIfAvailable();
        this.registryConfig = registryConfig.getIfAvailable();
    }

    @Bean
    @ConditionalOnBean({ApplicationConfig.class, RegistryConfig.class})
    public ChronusSdkProcessor chronusSdkFacade() {
        ChronusSdkProcessor chronusClientFacade = new AbstractSdkService(){};
        ServiceConfig<ChronusSdkProcessor> serviceConfig = new ServiceConfig<>();
        serviceConfig.setApplication(applicationConfig);
        serviceConfig.setRegistry(registryConfig);
        serviceConfig.setInterface(ChronusSdkProcessor.class);
        serviceConfig.setRef(chronusClientFacade);
        serviceConfig.setPath("/" + applicationConfig.getName() + "/" + ChronusSdkProcessor.class.getName());
        //serviceConfig.setGroup(applicationConfig.getName());
        serviceConfig.export();
        return chronusClientFacade;
    }
}
