package com.qihoo.finance.chronus.dispatcher;

/**
 * 提供批量处理接口
 *
 * @param <T> 任务类型
 * @author xiongpu
 */
public interface TaskBatchDealService<T> extends TaskDealService {

    /**
     * 执行批量任务
     *
     * @param task List<T>
     * @throws Exception
     */
    boolean execute(T[] task) throws Exception;
}
