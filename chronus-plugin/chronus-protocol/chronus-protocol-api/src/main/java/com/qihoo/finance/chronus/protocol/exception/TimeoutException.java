package com.qihoo.finance.chronus.protocol.exception;

import com.qihoo.finance.chronus.common.exception.ErrorCodeIntf;
import com.qihoo.finance.chronus.common.exception.ServiceException;

/**
 * Created by xiongpu on 2017/6/19.
 */
public class TimeoutException extends ServiceException {
    public TimeoutException() {
        super();
    }


    public TimeoutException(String code, String message) {
        super(code, message);
    }

    public TimeoutException(ErrorCodeIntf errorCodeEnum, String othMsg) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg()+othMsg);
    }
    public TimeoutException(ErrorCodeIntf errorCodeEnum) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    public TimeoutException(ErrorCodeIntf errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), cause);
    }

    public TimeoutException(ServiceException serviceException) {
        super(serviceException.getCode(), serviceException.getMessage());
    }

    public TimeoutException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public TimeoutException(ServiceException serviceException, Throwable cause) {
        super(serviceException.getCode(), serviceException.getMessage(), cause);
    }
}

