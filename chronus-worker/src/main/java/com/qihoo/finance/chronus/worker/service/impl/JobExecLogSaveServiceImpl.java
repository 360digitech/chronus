package com.qihoo.finance.chronus.worker.service.impl;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.common.util.BeanUtils;
import com.qihoo.finance.chronus.core.log.JobExecLogService;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.worker.config.WorkerProperties;
import com.qihoo.finance.chronus.worker.service.JobExecLogSaveService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/4/13.
 */
@Slf4j
public class JobExecLogSaveServiceImpl implements JobExecLogSaveService, ApplicationListener<ContextRefreshedEvent> {
    private static final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_WORKER,SupportConstants.SAVE_JOB_EXEC_LOG));

    @Resource
    private JobExecLogService jobExecLogService;

    @Resource
    private WorkerProperties workerProperties;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        scheduledExecutorService.scheduleWithFixedDelay(saveJobExecLogTask, workerProperties.getSaveJobExecLogTaskInitialDelay(), workerProperties.getSaveJobExecLogTaskDelay(), TimeUnit.SECONDS);
    }

    private ConcurrentLinkedQueue<JobExecLogEntity> concurrentLinkedQueue = new ConcurrentLinkedQueue();

    @Override
    public void sendExecLogToQueue(JobExecLogEntity jobExecLogEntity) {
        concurrentLinkedQueue.add(BeanUtils.copyBean(JobExecLogEntity.class, jobExecLogEntity));
    }

    private Runnable saveJobExecLogTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_WORKER, SupportConstants.SAVE_JOB_EXEC_LOG,false) {
        @Override
        public void process() throws Exception {
            int i = 0;
            List<JobExecLogEntity> jobExecLogEntities = new ArrayList<>();
            Long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < workerProperties.getSaveJobExecLogTime() * ChronusConstants.MILLISECOND_2_SECOND) {
                if (concurrentLinkedQueue.isEmpty()) {
                    Thread.sleep(100);
                    continue;
                }
                i++;
                JobExecLogEntity e = concurrentLinkedQueue.poll();
                jobExecLogEntities.add(e);
            }

            if (jobExecLogEntities.size() == 0) {
                return;
            }
            jobExecLogService.batchInsert(jobExecLogEntities);
            log.info("执行日志数据发送完成,total:{}", i);
        }
    };
}
