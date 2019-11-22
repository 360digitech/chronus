package com.qihoo.finance.chronus.common.util;

import com.qihoo.finance.chronus.common.domain.Response;
import org.springframework.validation.BindingResult;

/**
 * Created by xiongpu on 2019/8/15.
 */
public class ControllerUtil {

    public static Response checkResponse(Response response, BindingResult bindingResult){
        if(bindingResult.hasErrors()){
            return response.fail("",bindingResult.getFieldError().getDefaultMessage());
        }
        return response;
    }

}
