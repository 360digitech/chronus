package com.qihoo.finance.chronus.sdk.service;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkSingleJob<T> extends ChronusSdkSelectTaskService<T> {

    /**
     * 执行单个任务
     *
     * @param item
     * @param parameter
     * @return 执行结果
     * @throws Exception
     */
    boolean execute(T item, String parameter) throws Exception;
}
