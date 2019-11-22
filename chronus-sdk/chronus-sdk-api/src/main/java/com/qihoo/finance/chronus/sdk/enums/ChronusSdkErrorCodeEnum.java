package com.qihoo.finance.chronus.sdk.enums;

/**
 * Created by xiongpu on 2018/7/5.
 */
public enum ChronusSdkErrorCodeEnum {
    HANDLE_BEAN_NOT_EXIST("BCHRONUS000", "JobBean不存在!"),
    SELECT_TASKS_ERROR("BCHRONUS001", "获取处理任务列表异常!"),;

    private String code;
    private String msg;

    ChronusSdkErrorCodeEnum(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return this.code;
    }

    public String getMsg() {
        return this.msg;
    }
}
