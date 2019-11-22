package com.qihoo.finance.chronus.common.exception;


/**
 * Created by xiongpu on 2017/6/19.
 */
public class BusinessException extends ServiceException {
    public BusinessException() {
        super();
    }

    public BusinessException(ErrorCodeIntf errorCodeEnum) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg());
    }

    public BusinessException(ErrorCodeIntf errorCodeEnum,String othMsg) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg()+othMsg);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }

    public BusinessException(ServiceException serviceException) {
        super(serviceException.getCode(), serviceException.getMessage());
    }
    public BusinessException(ErrorCodeIntf errorCodeEnum, Throwable cause) {
        super(errorCodeEnum.getCode(), errorCodeEnum.getMsg(), cause);
    }

    public BusinessException(String code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public BusinessException(ServiceException serviceException, Throwable cause) {
        super(serviceException.getCode(), serviceException.getMessage(), cause);
    }

}
