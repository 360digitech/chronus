package com.qihoo.finance.chronus.common.exception;

/**
 * Created by xiongpu on 2018/7/5.
 */
public enum ChronusErrorCodeEnum implements ErrorCodeIntf {
    SYS_CODE_MATCHING_INVOKER_NOT_EXIST("BCHRONUS000", "select invokers error"),
    CALL_EXT_SYS_ERROR("BCHRONUS500", "调用业务系统异常!"),;

    ChronusErrorCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private String code;
    private String desc;


    @Override
    public String getCode() {
        return this.code;
    }

    @Override
    public String getMsg() {
        return this.desc;
    }
}
