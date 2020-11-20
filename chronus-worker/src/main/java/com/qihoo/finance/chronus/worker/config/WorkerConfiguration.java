package com.qihoo.finance.chronus.worker.config;

import com.qihoo.finance.chronus.support.WorkerSupport;
import com.qihoo.finance.chronus.worker.log.JobInvokeMonitorAspect;
import com.qihoo.finance.chronus.worker.processor.ExecuteProcessor;
import com.qihoo.finance.chronus.worker.processor.SelectExecuteFlowProcessor;
import com.qihoo.finance.chronus.worker.processor.SelectExecuteSimpleProcessor;
import com.qihoo.finance.chronus.worker.service.*;
import com.qihoo.finance.chronus.worker.service.impl.*;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@EnableConfigurationProperties({WorkerProperties.class})
@ConditionalOnProperty(prefix = "chronus.worker", name = {"enabled"}, matchIfMissing = true)
public class WorkerConfiguration {

    @Order(300)
    @Bean(name = "support#Worker")
    public WorkerSupport workerSupport() {
        WorkerSupport workerSupport = new WorkerSupport();
        return workerSupport;
    }

    @Bean
    public TaskManagerFactory taskManagerFactory() {
        TaskManagerFactory taskManagerFactory = new TaskManagerFactory();
        return taskManagerFactory;
    }

    @Bean
    public WorkerService workerService() {
        WorkerService workerService = new WorkerServiceImpl();
        return workerService;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public WorkerReceiveProcess workerReceiveProcess() {
        WorkerReceiveProcess workerReceiveProcess = new WorkerReceiveProcessImpl();
        return workerReceiveProcess;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public JobDispatcher jobDispatcher() {
        JobDispatcher jobDispatcher = new JobDispatcherImpl();
        return jobDispatcher;
    }

    @Bean
    public JobInvokeMonitorAspect JobInvokeMonitorAspect() {
        return new JobInvokeMonitorAspect();
    }

    @Bean
    @ConditionalOnProperty(prefix = "chronus.worker.log", name = {"plugin"}, havingValue = "default", matchIfMissing = true)
    public JobExecLogSaveService jobExecLogSaveService() {
        JobExecLogSaveService jobExecLogSaveService = new JobExecLogSaveServiceImpl();
        return jobExecLogSaveService;
    }

    @Bean(name = "Execute")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ScheduleProcessor Execute() {
        ScheduleProcessor scheduleProcessor = new ExecuteProcessor();
        return scheduleProcessor;
    }

    @Bean(name = "SelectExecuteSimple")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ScheduleProcessor SelectExecuteSimple() {
        ScheduleProcessor scheduleProcessor = new SelectExecuteSimpleProcessor();
        return scheduleProcessor;
    }

    @Bean(name = "SelectExecuteFlow")
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public ScheduleProcessor SelectExecuteFlow() {
        ScheduleProcessor scheduleProcessor = new SelectExecuteFlowProcessor();
        return scheduleProcessor;
    }
}
