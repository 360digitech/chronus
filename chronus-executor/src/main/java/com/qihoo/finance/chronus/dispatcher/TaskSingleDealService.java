package com.qihoo.finance.chronus.dispatcher;

/**
 * 提供单笔处理接口
 *
 * @param
 * @author xiongpu
 */
public interface TaskSingleDealService<T> extends TaskDealService {

    /**
     * 执行单个任务
     *
     * @param task Object
     * @throws Exception
     */
    boolean execute(T task) throws Exception;
}
