package com.qihoo.finance.chronus.protocol.exception;

import com.qihoo.finance.chronus.common.exception.ErrorCodeIntf;
import com.qihoo.finance.chronus.common.exception.ServiceException;

/**
 * Created by xiongpu on 2017/6/19.
 */
public class NoProviderException extends ServiceException {
    public NoProviderException() {
        super();
    }

    public NoProviderException(String code, String message) {
        super(code, message);
    }

    public NoProviderException(ErrorCodeIntf errorCodeEnum, String othMsg) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg()+othMsg);
    }
    public NoProviderException(ErrorCodeIntf errorCodeEnum) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    public NoProviderException(ErrorCodeIntf errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), cause);
    }

    public NoProviderException(ServiceException serviceException) {
        super(serviceException.getCode(), serviceException.getMessage());
    }

    public NoProviderException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public NoProviderException(ServiceException serviceException, Throwable cause) {
        super(serviceException.getCode(), serviceException.getMessage(), cause);
    }
}

