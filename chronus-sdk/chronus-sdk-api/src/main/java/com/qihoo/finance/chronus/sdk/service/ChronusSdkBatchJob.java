package com.qihoo.finance.chronus.sdk.service;

/**
 * Created by xiongpu on 2018/6/29.
 */
public interface ChronusSdkBatchJob<T> extends ChronusSdkSelectTaskService<T> {
    /**
     * 执行批量任务
     *
     * @param itemArr
     * @param parameter
     * @return 执行结果 会记录到执行日志
     * @throws Exception
     */
    boolean execute(T[] itemArr, String parameter) throws Exception;
}
