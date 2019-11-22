package com.qihoo.finance.chronus.executor.config;

import com.qihoo.finance.chronus.executor.service.ExecutorService;
import com.qihoo.finance.chronus.executor.service.JobExecLogSaveService;
import com.qihoo.finance.chronus.executor.service.TaskDaemonThreadService;
import com.qihoo.finance.chronus.executor.service.TaskHeartBeatService;
import com.qihoo.finance.chronus.executor.service.impl.*;
import com.qihoo.finance.chronus.support.ExecutorSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@EnableConfigurationProperties({ExecutorProperties.class})
@ConditionalOnProperty(prefix = "chronus.executor", name = {"enabled"}, matchIfMissing = true)
public class ExecutorConfiguration {

    @Order(300)
    @Bean(name = "support#Executor")
    public ExecutorSupport executorSupport() {
        ExecutorSupport executorSupport = new ExecutorSupport();
        return executorSupport;
    }

    @Bean
    public TaskManagerFactory taskManagerFactory() {
        TaskManagerFactory taskManagerFactory = new TaskManagerFactory();
        return taskManagerFactory;
    }

    @Bean
    public ExecutorService executorService() {
        ExecutorService executorService = new ExecutorServiceImpl();
        return executorService;
    }

    @Bean
    public TaskHeartBeatService taskHeartBeatService() {
        TaskHeartBeatService taskHeartBeatService = new TaskHeartBeatServiceImpl();
        return taskHeartBeatService;
    }

    @Bean
    public TaskDaemonThreadService taskDaemonThreadService() {
        TaskDaemonThreadService taskDaemonThreadService = new TaskDaemonThreadServiceImpl();
        return taskDaemonThreadService;
    }

    @Bean
    @ConditionalOnProperty(prefix = "chronus.executor.log", name = {"plugin"}, havingValue = "default", matchIfMissing = true)
    public JobExecLogSaveService jobExecLogSaveService() {
        JobExecLogSaveService jobExecLogSaveService = new JobExecLogSaveServiceImpl();
        return jobExecLogSaveService;
    }

}
