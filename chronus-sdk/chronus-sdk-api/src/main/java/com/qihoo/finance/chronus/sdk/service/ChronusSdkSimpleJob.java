package com.qihoo.finance.chronus.sdk.service;

import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;

import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkSimpleJob {

    /**
     * 简单任务
     *
     * @param taskParameter
     * @param taskItemList
     * @param eachFetchDataNum
     * @return 执行结果
     * @throws Exception
     */
    boolean execute(String taskParameter, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception;
}
