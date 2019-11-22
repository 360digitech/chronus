package com.qihoo.finance.chronus.context;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 服务上下文
 * 环境变量只存放不会变更的少部分变量信息，如用户IP，MAC等环境信息
 * <p>
 * Created by xiongpu on 2017/6/28.
 */
public class ServiceContext {
    // 定义上下文
    private static final ThreadLocal<ServiceContext> CONTEXT = new InheritableThreadLocal<ServiceContext>() {
        @Override
        protected ServiceContext childValue(ServiceContext parentValue) {
            ServiceContext context = new ServiceContext();
            if (parentValue != null) {
                context.initBy(parentValue);
            }
            return context;
        }

        @Override
        protected ServiceContext initialValue() {
            return new ServiceContext();
        }
    };

    /**
     * 服务上下文环境变量
     */
    private Map<String, String> contextVar = new ConcurrentHashMap<>();

    /**
     * 获取当前线程的上下文
     *
     * @return
     */
    public static ServiceContext getContext() {
        return CONTEXT.get();
    }

    /**
     * 获取服务上下文，并设置请求流水号前缀
     *
     * @param prefix
     * @return
     */
    public static ServiceContext getContext(String prefix) {
        if (StringUtils.isBlank(prefix)) {
            return getContext();
        }
        getContext().setRequestNo(prefix + genUniqueId());
        return getContext();
    }

    /**
     * 获取全局唯一请求流水号
     *
     * @return
     */
    public static String genUniqueId() {
        return UUID.randomUUID().toString().replace("-", "");
    }

    /**
     * 清理上下文环境变量
     * 仅框架底层使用
     */
    public ServiceContext clearContextVar() {
        contextVar.clear();
        return getContext();
    }

    /**
     * 初始化上下文环境变量，copy源ServiceContext
     *
     * @param serviceContext
     */
    public void initBy(ServiceContext serviceContext) {
        if (serviceContext == null || serviceContext == this) {
            return;
        }

        contextVar.clear();
        contextVar.putAll(serviceContext.getContextVar());
    }

    /**
     * 根据key清理上下文环境变量
     *
     * @param key
     */
    public void removeContextVar(String key) {
        if (contextVar.containsKey(key)) {
            contextVar.remove(key);
        }
    }

    /**
     * 获取上下文变量Map对象
     *
     * @return
     */
    public Map<String, String> getContextVar() {
        return contextVar;
    }

    /**
     * 根据key获取上下文变量值
     *
     * @param key
     * @return
     */
    public String getContextVar(String key) {
        return contextVar.get(key);
    }

    /**
     * 添加上下文环境变量
     *
     * @param key
     * @param value
     */
    public ServiceContext addContextVar(String key, String value) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return getContext();
        }
        contextVar.put(key, value);
        return this;
    }

    /**
     * 批量添加上下文环境变量
     *
     * @param vars
     */
    public ServiceContext addContextVar(Map<String, String> vars) {
        if (vars != null && vars.keySet().size() > 0) {
            contextVar.putAll(vars);
        }
        return this;
    }


    public String getConsumerIp() {
        return this.getContextVar(ContextConstKey.CONSUMER_IP);
    }

    public ServiceContext setConsumerIp(String consumerIp) {
        this.addContextVar(ContextConstKey.CONSUMER_IP, consumerIp);
        return this;
    }

    public ServiceContext setLocalIp(String localIp) {
        this.addContextVar(ContextConstKey.LOCAL_IP, localIp);
        return this;
    }

    public String getlocalIp() {
        return this.getContextVar(ContextConstKey.LOCAL_IP);
    }

    public String getRequestNo() {
        return this.getContextVar(ContextConstKey.REQUEST_NO);
    }

    public ServiceContext setRequestNo(String requestNo) {
        this.addContextVar(ContextConstKey.REQUEST_NO, requestNo);
        return this;
    }

    public String getSysCode() {
        return this.getContextVar(ContextConstKey.SYS_CODE);
    }

    public ServiceContext setSysCode(String sysCode) {
        this.addContextVar(ContextConstKey.SYS_CODE, sysCode);
        return this;
    }

    public String getBeanName() {
        return this.getContextVar(ContextConstKey.BEAN_NAME);
    }



    public ServiceContext setBeanName(String beanName) {
        this.addContextVar(ContextConstKey.BEAN_NAME, beanName);
        return this;
    }
}