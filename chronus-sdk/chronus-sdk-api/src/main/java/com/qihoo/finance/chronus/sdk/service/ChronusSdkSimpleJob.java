package com.qihoo.finance.chronus.sdk.service;

import com.qihoo.finance.chronus.sdk.domain.JobData;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkSimpleJob {

    /**
     * 简单任务
     *
     * @param jobData
     * @return 执行结果
     * @throws Exception
     */
    boolean execute(JobData jobData) throws Exception;
}
