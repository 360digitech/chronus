package com.qihoo.finance.chronus.sdk.service;

import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;

import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkSelectTaskService<T>  {

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     * @param taskParameter        任务参数
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     * @throws Exception
     */
    List<T> selectTasks(String taskParameter, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception ;
}
