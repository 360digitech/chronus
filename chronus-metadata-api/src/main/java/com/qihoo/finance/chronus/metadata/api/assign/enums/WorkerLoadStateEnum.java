package com.qihoo.finance.chronus.metadata.api.assign.enums;

/**
 * Created by xiongpu on 2019/9/7.
 */
public enum WorkerLoadStateEnum {
    /**
     * worker初始化为READY状态
     * 状态上报完成转成 READY
     */
    READY,
    /**
     * 需要加载或者停止任务时Master会将当前节点置为RESET状态,
     * 任务分配时如果节点处于RESET状态,则需要等待处理完成,才能继续分配
     */
    RESET,
    /**
     * worker 处理完成 状态转成 FINISH
     */
    FINISH,

}
