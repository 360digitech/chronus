package com.qihoo.finance.chronus.common.handler;

import com.qihoo.finance.chronus.common.domain.Response;
import lombok.extern.log4j.Log4j2;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 *
 * @author chenxiyu
 * @date 2019/9/17
 */
@Log4j2
@RestControllerAdvice
public class GlobalExceptionHandler {


    /**
     * 处理运行异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Response handleRuntimeException(RuntimeException ex) {
        log.error("", ex);
        Response response = new Response();
        return  response.hinderFail(ex.getMessage());
    }

    /**
     *
     * @param ex
     * @return
     * @throws Exception
     */
    @ExceptionHandler(value = Exception.class)
    public Response defaultErrorHandler(Exception ex) {
        log.error("", ex);
        Response response = new Response();
        response.setMsg(ex.getMessage());
        if (ex instanceof NoHandlerFoundException) {
            response.setCode("404");
        } else {
            response.setCode("500");
        }
        response.setFlag("F");
        return response;
    }
}
