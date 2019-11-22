package com.qihoo.finance.chronus.dispatcher;

import com.qihoo.finance.chronus.metadata.api.task.bo.TaskItemDefine;

import java.util.List;

/**
 *
 * 提供选择器
 * Created by xiongpu on 2019/11/6.
 */
public interface TaskSelectTaskService<T> {

    /**
     * 根据条件，查询当前调度服务器可处理的任务
     *
     * @param taskParameter    任务的自定义参数
     * @param taskItemList     当前调度服务器，分配到的可处理队列
     * @param eachFetchDataNum 每次获取数据的数量
     * @return
     * @throws Exception
     */
    List<T> selectTasks(String taskParameter, List<TaskItemDefine> taskItemList, int eachFetchDataNum) throws Exception;
}
