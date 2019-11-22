package com.qihoo.finance.chronus.common;

import com.qihoo.finance.chronus.context.SpringContextHolder;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.shiro.web.filter.AccessControlFilter;
import org.springframework.core.env.Environment;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/9/16.
 */
public class TokenFilter extends AccessControlFilter {

    @Override
    protected void postHandle(ServletRequest request, ServletResponse response) {
        //设置一个标记位
        request.setAttribute("TokenFilter.FILTERED", true);
    }


    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        Boolean afterFiltered = (Boolean) (servletRequest.getAttribute("TokenFilter.FILTERED"));
        if (BooleanUtils.isTrue(afterFiltered)) {
            return true;
        }
        String token = SpringContextHolder.getBean(Environment.class).getProperty(ChronusConstants.API_TOKEN_KEY);
        String reqToken = ((HttpServletRequest) servletRequest).getHeader(ChronusConstants.API_TOKEN_KEY);
        return Objects.equals(token, reqToken);
    }

    @Override
    protected boolean onAccessDenied(ServletRequest servletRequest, ServletResponse servletResponse) throws Exception {
        return false;
    }
}
