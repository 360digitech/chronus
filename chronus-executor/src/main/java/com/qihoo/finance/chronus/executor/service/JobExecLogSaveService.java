package com.qihoo.finance.chronus.executor.service;

import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;

/**
 * Created by xiongpu on 2019/4/13.
 */
public interface JobExecLogSaveService {

    /**
     * 将job执行信息放入内存队列
     * @param jobExecLogEntity
     */
    void sendExecLogToQueue(JobExecLogEntity jobExecLogEntity);
}


