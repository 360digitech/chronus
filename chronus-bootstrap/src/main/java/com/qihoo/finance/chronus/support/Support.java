package com.qihoo.finance.chronus.support;

/**
 * Created by xiongpu on 2019/8/5.
 */
public interface Support {
    /**
     * 启动节点
     *
     * @throws Exception
     */
    void start() throws Exception;

    /**
     * 停止节点
     *
     * @throws Exception
     */
    void stop() throws Exception;
}
