package com.qihoo.finance.chronus.dispatcher;

import com.qihoo.finance.chronus.metadata.api.task.bo.TaskItemDefine;

import java.util.List;

/**
 * 提供简单触发接口
 *
 * @param
 * @author xiongpu
 */
public interface TaskSimpleDealService extends TaskDealService {

    /**
     * 执行单个任务
     *
     * @param
     * @throws Exception
     */
    boolean execute(String taskParameter, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception;
}
