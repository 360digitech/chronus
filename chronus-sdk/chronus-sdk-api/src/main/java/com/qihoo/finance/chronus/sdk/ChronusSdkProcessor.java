package com.qihoo.finance.chronus.sdk;

import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;

import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkProcessor<T> {
    /**
     * 获取客户端版本
     * @return
     */
    String getVersion();

    /**
     * 执行单个任务
     *
     * @param jobConfig job配置
     * @param item      item
     * @return 执行结果
     */
    boolean execute(JobConfig jobConfig, T item);

    /**
     * 直接触发
     * @param jobConfig
     * @param taskItemList
     * @param eachFetchDataNum
     * @return 执行结果
     * @throws Exception
     */
    boolean execute(JobConfig jobConfig, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception;

    boolean executeBatch(JobConfig jobConfig, T[] item);

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param jobConfig        job配置
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return 筛选的数据列表
     * @throws Exception
     */
    List<T> selectTasks(JobConfig jobConfig, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception;
}