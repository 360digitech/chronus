package com.qihoo.finance.chronus.common.filter;

import com.alibaba.fastjson.JSONObject;
import com.qihoo.finance.chronus.common.domain.Response;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.web.filter.AccessControlFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/**
 * @author: chenxiyu
 * @date: 2020-01-16 21:39
 **/
public class CsrfFilter extends AccessControlFilter {
    @Override
    protected boolean isAccessAllowed(ServletRequest request, ServletResponse response, Object mappedValue) throws Exception {
        HttpServletRequest httpServletRequest = (HttpServletRequest)request;
        String authorization = httpServletRequest.getHeader("Authorization");
        String token = httpServletRequest.getSession().getId();
        if (!StringUtils.equals(token,authorization)) {
            Response result = new Response().success();
            result.setMsg("Unauthorized token expired");
            response.getWriter().write(JSONObject.toJSON(result).toString());
            return false;
        }
        return true;
    }

    @Override
    protected boolean onAccessDenied(ServletRequest request, ServletResponse response) throws Exception {
        return false;
    }
}
