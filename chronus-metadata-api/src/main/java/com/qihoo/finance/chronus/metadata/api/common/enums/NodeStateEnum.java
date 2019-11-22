package com.qihoo.finance.chronus.metadata.api.common.enums;

import lombok.Getter;

/**
 * Created by xiongpu on 2019/7/31.
 */
@Getter
public enum NodeStateEnum {

    INIT("INIT","初始化"),
    NORMAL("NORMAL","正常"),
    /**
     * 人工操作下线
     */
    OFFLINE("OFFLINE","下线"),
    /**
     * 心跳暂停 无法连接
     */
    DEAD("DEAD","死亡"),
    ;

    private NodeStateEnum(String state, String desc){
        this.state=state;
        this.desc=desc;
    }
    private String state;
    private String desc;

    public static boolean isNormal(String state){
        return NodeStateEnum.NORMAL.getState().equals(state);
    }

    public static boolean isOffline(String state){
        return NodeStateEnum.OFFLINE.getState().equals(state);
    }

    public static boolean isDead(String state){
        return NodeStateEnum.DEAD.getState().equals(state);
    }
}
