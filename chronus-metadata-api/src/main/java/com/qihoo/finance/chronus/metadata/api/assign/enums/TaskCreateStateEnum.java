package com.qihoo.finance.chronus.metadata.api.assign.enums;

/**
 * Created by xiongpu on 2019/9/7.
 */
public enum TaskCreateStateEnum {

    INIT("INIT","初始化"),
    SUCC("SUCC","加载成功"),
    FAIL("FAIL","加载失败"),
    ;

    private String state;
    private String desc;
    TaskCreateStateEnum(String state, String desc){
        this.state=state;
        this.desc=desc;
    }

    public String getState() {
        return state;
    }

    public String getDesc() {
        return desc;
    }
}
