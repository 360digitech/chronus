package com.qihoo.finance.chronus.sdk;

import com.qihoo.finance.chronus.sdk.domain.JobData;

import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkFacade<T> {
    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param jobData job配置
     * @return 筛选的数据列表
     * @throws Exception
     */
    List<T> selectTasks(JobData jobData) throws Exception;

    /**
     * 执行单个任务
     *
     * @param jobData  job配置
     * @param itemList item
     * @return 执行结果
     */
    boolean execute(JobData jobData, List<T> itemList) throws Exception;

    /**
     * 直接触发
     *
     * @param jobData
     * @return 执行结果
     * @throws Exception
     */
    boolean execute(JobData jobData) throws Exception;
}