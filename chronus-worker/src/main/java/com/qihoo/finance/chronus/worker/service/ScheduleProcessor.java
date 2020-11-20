package com.qihoo.finance.chronus.worker.service;

import com.qihoo.finance.chronus.worker.bo.ProcessorParam;

public interface ScheduleProcessor {
    /**
     * 初始化ProcessorParam
     * @param param
     * @return
     */
    ScheduleProcessor init(ProcessorParam param);

    /**
     * 重置ProcessorParam.setExecutorEnd=false
     * @return
     */
    ScheduleProcessor start();

    ProcessorParam getProcessorParam();

    /**
     * 启动调度工作线程
     * 将当前process丢入线程池
     * @return
     */
    void startThread();

    /**
     * 是否已经执行结束
     *
     * @return
     */
    boolean isExecutorEnd();

    /**
     * 停止任务处理器
     *
     * @throws Exception
     */
    void shutdown();
}
