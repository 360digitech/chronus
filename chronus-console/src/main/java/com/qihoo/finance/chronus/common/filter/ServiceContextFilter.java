package com.qihoo.finance.chronus.common.filter;

import com.qihoo.finance.chronus.common.util.WebUtils;
import com.qihoo.finance.chronus.context.ContextLog4j2Util;
import com.qihoo.finance.chronus.context.ServiceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Created by xiongpu on 2016/9/8.
 */
@Slf4j
public class ServiceContextFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest = serviceContextInit(servletRequest);
        try {
            filterChain.doFilter(servletRequest, servletResponse);
        } catch (Exception e) {
            throw e;
        } finally {
            ContextLog4j2Util.removeContextFromThreadContext();
        }
    }

    private ServletRequest serviceContextInit(ServletRequest servletRequest) throws IOException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            ServiceContext context = ServiceContext.getContext();
            context.clearContextVar(); //每次处理请求先清理上下文
            context.setRequestNo(ServiceContext.genUniqueId());

            String clientRealIp = ((HttpServletRequest) servletRequest).getHeader("X-Real-IP");
            context.setConsumerIp(StringUtils.isEmpty(clientRealIp) ? WebUtils.getRemoteAddr(httpServletRequest) : clientRealIp);
            context.setLocalIp(httpServletRequest.getLocalAddr());

            ContextLog4j2Util.addContext2ThreadContext();
        } catch (Exception e) {
            log.warn("Service Init fail! [url={}?{}]", ((HttpServletRequest) servletRequest).getRequestURL(), ((HttpServletRequest) servletRequest).getQueryString(), e);
        }
        return servletRequest;
    }
}
