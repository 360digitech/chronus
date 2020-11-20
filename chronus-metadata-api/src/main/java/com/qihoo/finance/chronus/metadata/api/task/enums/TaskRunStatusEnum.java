package com.qihoo.finance.chronus.metadata.api.task.enums;

/**
 * Created by xiongpu on 2019/11/6.
 */
public enum TaskRunStatusEnum {
    /**
     * 初始化完成等待分配
     */
    INIT,
    STOP,
    /**
     * 死亡
     */
    DEAD,
    /**
     * 等待中
     * 处于调度间隔周期内
     */
    WAITING,
    /**
     * 等待中
     * 任务处于睡眠状态
     */
    SLEEP,
    /**
     * 运行中
     */
    RUNNING,
    /**
     * 异常错误
     */
    ERROR;

    public static boolean isRuntimeStatus(String status) {
        return !TaskRunStatusEnum.INIT.name().equals(status);
    }
    public static boolean isWaitingStatus(String status) {
        return TaskRunStatusEnum.WAITING.name().equals(status);
    }
    public static boolean isErrorStatus(String status) {
        return TaskRunStatusEnum.ERROR.name().equals(status);
    }
}
