package com.qihoo.finance.chronus.common.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by xiongpu on 2019/11/18.
 */
@Slf4j
public class CsrfInterceptor extends HandlerInterceptorAdapter {

    private static final String HEADER_REFERER_KEY = "referer";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String referrer = request.getHeader(HEADER_REFERER_KEY);
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append(request.getScheme()).append("://").append(request.getServerName());
        if (StringUtils.isBlank(referrer) || referrer.lastIndexOf(String.valueOf(stringBuffer)) == 0) {
            return true;
        } else {
            log.info("referrer:{},basePath:{}", referrer, stringBuffer);
            return false;
        }
    }
}
