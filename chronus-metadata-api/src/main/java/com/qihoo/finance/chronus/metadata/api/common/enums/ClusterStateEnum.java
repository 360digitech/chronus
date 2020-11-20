package com.qihoo.finance.chronus.metadata.api.common.enums;

import lombok.Getter;

/**
 * Created by xiongpu on 2019/7/31.
 */
@Getter
public enum ClusterStateEnum {

    NORMAL("NORMAL", "正常"),
    /**
     * 容灾状态,运行所有集群的任务
     */
    DATA_GUARD("DATA_GUARD", "容灾"),
    /**
     * 关闭,停止所有调度任务
     */
    CLOSE("CLOSE", "关闭"),
    ;

    private ClusterStateEnum(String state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    private String state;
    private String desc;

    public static boolean isNormal(String state) {
        return ClusterStateEnum.NORMAL.getState().equals(state);
    }

    public static boolean isDataGuard(String state) {
        return ClusterStateEnum.DATA_GUARD.getState().equals(state);
    }

    public static boolean isClose(String state) {
        return ClusterStateEnum.CLOSE.getState().equals(state);
    }
}
