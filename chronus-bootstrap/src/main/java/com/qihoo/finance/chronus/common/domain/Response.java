package com.qihoo.finance.chronus.common.domain;


import com.qihoo.finance.chronus.common.exception.ErrorCodeIntf;

/**
 * Created by xiongpu on 2017/6/19.
 */
public class Response<T> extends Domain {
    private static final long serialVersionUID = -6134809657927654993L;

    public static final String SUCC = "S";
    public static final String FAIL = "F";

    private String flag;
    private String code;
    private String msg;
    private T data;

    public Response() {
    }

    public Response(T data) {
        this.data = data;
    }

    public Response success() {
        this.flag = SUCC;
        return this;
    }

    public Response success(T data) {
        this.data = data;
        return success();
    }

    public Response fail() {
        this.flag = FAIL;
        return this;
    }

    public Response hinderFail(String msg) {
        this.flag = FAIL;
        this.code = FAIL;
        this.msg = msg;
        return this;
    }

    public Response fail(T data) {
        this.data = data;
        return fail();
    }

    public Response fail(ErrorCodeIntf err) {
        this.code = err.getCode();
        this.msg = err.getMsg();
        return fail();
    }

    public Response fail(ErrorCodeIntf err, String othMsg) {
        this.code = err.getCode();
        this.msg = err.getMsg() + othMsg;
        return fail();
    }

    public Response fail(String code, String msg) {
        this.code = code;
        this.msg = msg;
        return fail();
    }


    /**
     * 处理成功?
     *
     * @return
     */
    public boolean successful() {
        return SUCC.equals(this.flag);
    }

    /**
     * 处理失败?
     *
     * @return
     */
    public boolean failed() {
        return FAIL.equals(this.flag);
    }

    public String getFlag() {
        return flag;
    }

    public Response<T> setFlag(String flag) {
        this.flag = flag;
        return this;
    }

    public String getCode() {
        return code;
    }

    public Response<T> setCode(String code) {
        this.code = code;
        return this;
    }

    public String getMsg() {
        return msg;
    }

    public Response setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public Response setData(T data) {
        this.data = data;
        return this;
    }

}
