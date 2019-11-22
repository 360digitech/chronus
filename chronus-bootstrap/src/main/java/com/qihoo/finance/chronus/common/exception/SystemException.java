package com.qihoo.finance.chronus.common.exception;

/**
 * Created by xiongpu on 2017/6/19.
 */
public class SystemException extends ServiceException {
    public SystemException() {
        super();
    }


    public SystemException(String code, String message) {
        super(code, message);
    }

    public SystemException(ErrorCodeIntf errorCodeEnum,String othMsg) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg()+othMsg);
    }
    public SystemException(ErrorCodeIntf errorCodeEnum) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    public SystemException(ErrorCodeIntf errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), cause);
    }

    public SystemException(ServiceException serviceException) {
        super(serviceException.getCode(), serviceException.getMessage());
    }

    public SystemException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public SystemException(ServiceException serviceException, Throwable cause) {
        super(serviceException.getCode(), serviceException.getMessage(), cause);
    }
}

