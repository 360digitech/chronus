package com.qihoo.finance.chronus.common.exception;

/**
 * Created by xiongpu on 2018/7/5.
 */
public enum ChronusErrorCodeEnum implements ErrorCodeIntf {
    CALL_BIZ_SYS_ERROR("C-SYS-500", "调用业务系统异常!"),
    CALL_BIZ_NO_PROVIDER("C-SYS-501", "未找到服务提供者!"),
    CALL_BIZ_TIMEOUT("C-SYS-502", "调用业务系统超时!"),
    ;

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
