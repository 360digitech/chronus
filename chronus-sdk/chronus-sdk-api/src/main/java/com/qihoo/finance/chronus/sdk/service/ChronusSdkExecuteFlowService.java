package com.qihoo.finance.chronus.sdk.service;

import com.qihoo.finance.chronus.sdk.domain.JobData;

import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkExecuteFlowService<T> {

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param jobData 任务参数
     * @return
     * @throws Exception
     */
    List<T> selectTasks(JobData jobData) throws Exception;

    /**
     * 执行批量任务
     * itemList大小取决于任务配置的每次处理数量
     *
     * @param jobData
     * @param itemList
     * @return 执行结果 会记录到执行日志
     * @throws Exception
     */
    boolean execute(JobData jobData, List<T> itemList) throws Exception;
}
