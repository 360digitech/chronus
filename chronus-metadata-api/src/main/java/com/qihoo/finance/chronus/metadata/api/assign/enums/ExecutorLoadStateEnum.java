package com.qihoo.finance.chronus.metadata.api.assign.enums;

/**
 * Created by xiongpu on 2019/9/7.
 */
public enum ExecutorLoadStateEnum {

    INIT("INIT", "初始化"),
    RESET("RESET", "已重置"),
    WAITING("WAITING", "等待中"),
    SUCC("SUCC", "加载成功"),
    FAIL("FAIL", "加载失败"),;

    private String state;
    private String desc;

    ExecutorLoadStateEnum(String state, String desc) {
        this.state = state;
        this.desc = desc;
    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }

    public boolean isEquals(String state) {
        return state != null && this.getState().equals(state);
    }
}
