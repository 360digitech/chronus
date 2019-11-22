package com.qihoo.finance.chronus.executor.config;

import com.qihoo.finance.chronus.sdk.ChronusSdkProcessor;
import org.apache.commons.lang3.StringUtils;
import com.qihoo.finance.chronus.config.BootstrapProperties;
import com.qihoo.finance.chronus.dispatcher.dubbo.DubboJobDispatcherImpl;
import org.apache.dubbo.config.*;
import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@AutoConfigureAfter(DubboAutoConfiguration.class)
@EnableConfigurationProperties({BootstrapProperties.class})
@ConditionalOnProperty(prefix = "dubbo", name = {"enabled"}, matchIfMissing = true)
public class DubboConfiguration {

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.application")
    public ApplicationConfig applicationConfig(Environment environment) {
        ApplicationConfig applicationConfig = new ApplicationConfig();
        String applicationName = environment.getProperty("dubbo.application.name");
        applicationName = StringUtils.isBlank(applicationName) ? environment.getProperty("spring.application.name") : applicationName;
        applicationConfig.setName(applicationName);
        return applicationConfig;
    }

    @Bean("defaultRegistry")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.registry")
    public RegistryConfig registryConfig() {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setProtocol("zookeeper");
        registryConfig.setAddress("127.0.0.1:2181");
        return registryConfig;
    }

    @Bean("dubbo")
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.protocol")
    public ProtocolConfig protocolConfig() {
        ProtocolConfig protocolConfig = new ProtocolConfig();
        protocolConfig.setName("dubbo");
        protocolConfig.setHost(null);
        protocolConfig.setPort(20880);
        protocolConfig.setSerialization("hessian2");
        return protocolConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.provider")
    public ProviderConfig providerConfig(ProtocolConfig dubbo, RegistryConfig registryConfig) {
        ProviderConfig providerConfig = new ProviderConfig();
        providerConfig.setProtocol(dubbo);
        providerConfig.setRegistry(registryConfig);
        providerConfig.setFilter("-exception");
        providerConfig.setPayload(20971520);
        providerConfig.setCluster("ChronusCFOB");
        return providerConfig;
    }

    @Bean
    @ConditionalOnMissingBean
    @ConfigurationProperties(prefix = "dubbo.consumer")
    public ConsumerConfig consumerConfig(RegistryConfig registryConfig) {
        ConsumerConfig consumerConfig = new ConsumerConfig();
        consumerConfig.setRegistry(registryConfig);
        consumerConfig.setFilter("-exception");
        consumerConfig.setCluster("failfast");
        consumerConfig.setCheck(false);
        consumerConfig.setTimeout(30000);
        consumerConfig.setCluster("ChronusCFOB");
        return consumerConfig;
    }


    @Bean(name = "chronusSdkFacade")
    @ConditionalOnMissingBean
    public ChronusSdkProcessor chronusSdkFacade(ApplicationConfig applicationConfig, RegistryConfig registryConfig) {
        ReferenceConfig<ChronusSdkProcessor> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(ChronusSdkProcessor.class);
        reference.setTimeout(30000);
        reference.setCheck(false);
        reference.setCluster("ChronusCFOB");
        //reference.setGroup("*");
        return reference.get();
    }

    @Bean(name = "DUBBO")
    @Scope("prototype")
    @ConditionalOnMissingBean
    public DubboJobDispatcherImpl jobByDubboType(ChronusSdkProcessor chronusSdkFacade) {
        DubboJobDispatcherImpl dubboJobDispatcher = new DubboJobDispatcherImpl(chronusSdkFacade);
        return dubboJobDispatcher;
    }

}
