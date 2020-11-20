package com.qihoo.finance.chronus.registry.config;

import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.local.MasterElectionServiceLocalImpl;
import com.qihoo.finance.chronus.registry.local.NamingServiceLocalImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiongpu on 2020/7/26.
 */
@Configuration
@EnableConfigurationProperties({RegistryLocalProperties.class})
@ConditionalOnProperty(prefix = "chronus", name = {"registry"}, havingValue = "local", matchIfMissing = true)
public class RegistryLocalConfiguration {

    /**
     * 基于本地的Master策略选举类
     *
     * @return
     */
    @Bean("masterElectionStrategy#local")
    public MasterElectionService masterElectionStrategyDefaultService() {
        MasterElectionService masterElectionStrategyService = new MasterElectionServiceLocalImpl();
        return masterElectionStrategyService;
    }

    @Bean
    public NamingService namingService() {
        NamingService namingService = new NamingServiceLocalImpl();
        return namingService;
    }
}
