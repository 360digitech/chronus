package com.qihoo.finance.chronus.master.config;

import com.qihoo.finance.chronus.master.service.TaskAssignService;
import com.qihoo.finance.chronus.master.service.impl.TaskAssignServiceImpl;
import com.qihoo.finance.chronus.support.MasterSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@EnableConfigurationProperties({MasterProperties.class})
@ConditionalOnProperty(prefix = "chronus.master", name = {"enabled"}, matchIfMissing = true)
public class MasterConfiguration {

    @Order(200)
    @Bean(name = "support#Master")
    public MasterSupport masterSupport() {
        MasterSupport masterSupport = new MasterSupport();
        return masterSupport;
    }

    @Bean
    public TaskAssignService taskAssignService() {
        TaskAssignService taskAssignService = new TaskAssignServiceImpl();
        return taskAssignService;
    }
}
