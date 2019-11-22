package com.qihoo.finance.chronus.context;


import com.qihoo.finance.chronus.common.util.NetUtils;
import org.apache.commons.lang3.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.ThreadContext;

/**
 * Log4j2打印上下文变量处理类
 * 将aby上下文中的一些数据加入log4j2的上下文中
 * Created by xiongpu on 2017/7/5.
 */
@Slf4j
public final class ContextLog4j2Util {
    /**
     * 将上下文环境变量信息加入到ThreadContext中，以便打印到日志
     */
    public static void addContext2ThreadContext() {
        ServiceContext context = ServiceContext.getContext();
        if (context == null) {
            log.warn("上下文未处理化，context is null");
            return;
        }
        ThreadContext.put(ContextConstKey.REQUEST_NO, StringUtils.isEmpty(context.getRequestNo()) ? "" : context.getRequestNo());
        ThreadContext.put(ContextConstKey.SYS_CODE, StringUtils.isEmpty(context.getSysCode()) ? "" : context.getSysCode());
        ThreadContext.put(ContextConstKey.LOCAL_IP, NetUtils.getLocalIP());
        ThreadContext.put(ContextConstKey.BEAN_NAME, StringUtils.isEmpty(context.getBeanName()) ? "" : context.getBeanName());
    }

    public static void addContext2ThreadContext(ServiceContext context) {
        if (context == null) {
            log.warn("上下文未处理化，context is null");
        } else {
            ThreadContext.put(ContextConstKey.REQUEST_NO, StringUtils.isEmpty(context.getRequestNo()) ? "" : context.getRequestNo());
            ThreadContext.put(ContextConstKey.SYS_CODE, StringUtils.isEmpty(context.getSysCode()) ? "" : context.getSysCode());
            ThreadContext.put(ContextConstKey.LOCAL_IP, NetUtils.getLocalIP());
            ThreadContext.put(ContextConstKey.BEAN_NAME, StringUtils.isEmpty(context.getBeanName()) ? "" : context.getBeanName());
        }
    }

    /**
     * 从ThreadContext清理对象
     */
    public static void removeContextFromThreadContext() {
        ThreadContext.remove(ContextConstKey.REQUEST_NO);
        ThreadContext.remove(ContextConstKey.CONSUMER_IP);
    }

    public static String getRequestNo(){
        return ServiceContext.getContext().getRequestNo();
    }

}
