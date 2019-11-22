package com.qihoo.finance.chronus.common.exception;


/**
 * Created by xiongpu on 2017/6/26.
 */
public interface ErrorCodeIntf {

    /**
     * 获取错误编码
     * @return
     */
    String getCode();

    /**
     * 获取错误描述
     * @return
     */
    String getMsg();

}
