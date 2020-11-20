package com.qihoo.finance.chronus.metadata.api.assign.enums;

/**
 * Created by xiongpu on 2019/9/7.
 */
public enum TaskItemStateEnum {
    /**
     * 初始化完成等待分配
     */
    INIT,
    /**
     * 分配结果状态
     */
    WAIT_STOP,
    WAIT_START,
    /**
     * 启动状态
     */
    STOP,
    START,
    /**
     * 异常错误
     */
    ERROR;

    public static boolean isStop(String status) {
        return STOP.name().equals(status);
    }

    public static boolean isWaitStop(String status) {
        return WAIT_STOP.name().equals(status);
    }

    public static boolean isStart(String status) {
        return START.name().equals(status);
    }
    public static boolean isInit(String status) {
        return INIT.name().equals(status);
    }
    public static boolean isError(String status) {
        return ERROR.name().equals(status);
    }

    public static boolean isWaitStart(String status) {
        return WAIT_START.name().equals(status);
    }
}
