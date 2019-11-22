package com.qihoo.finance.chronus.common;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

@Configuration
public class ShiroConfiguration {
    //将自己的验证方式加入容器
    @Bean
    public MyShiroRealm myShiroRealm() {
        MyShiroRealm myShiroRealm = new MyShiroRealm();
        return myShiroRealm;
    }

    @Bean
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setGlobalSessionTimeout(3600000L * 24 * 7);
        sessionManager.setDeleteInvalidSessions(true);
        sessionManager.setSessionValidationSchedulerEnabled(true);
        sessionManager.setSessionValidationInterval(3600000);
        return sessionManager;
    }

    @Bean
    public DefaultWebSecurityManager securityManager(MyShiroRealm myShiroRealm, DefaultWebSessionManager sessionManager) {
        DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
        manager.setRealm(myShiroRealm);
        manager.setSessionManager(sessionManager);
        return manager;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, Environment env) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置SecuritManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        //配置拦截器,实现无权限返回401,而不是跳转到登录页
        filters.put("tokenFilter", new TokenFilter());
        shiroFilterFactoryBean.setFilters(filters);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl(env.getProperty("shiro.loginUrl"));
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl(env.getProperty("shiro.successUrl"));
        // 未授权界面;
        shiroFilterFactoryBean.setUnauthorizedUrl(env.getProperty("shiro.unauthorizedUrl"));
        // 拦截器
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<>();
        // 过滤链定义，从上向下顺序执行，一般将 /**放在最为下边
        // authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问
        filterChainDefinitionMap.put("/favicon.ico", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/fonts/**", "anon");
        filterChainDefinitionMap.put("/img/**", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/html/**", "anon");
        filterChainDefinitionMap.put("/static/**", "anon");


        //配置退出 过滤器,其中的具体的退出代码Shiro已经替我们实现了
        filterChainDefinitionMap.put("/", "anon");
        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/logout", "logout");
        //filterChainDefinitionMap.put("/node/**", "tokenFilter");
        //过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/api/**", "authc");
        //<!-- authc:所有url都必须认证通过才可以访问; anon:所有url都都可以匿名访问-->
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        //这里添加一下对DispatcherType.ASYNC的支持就可以了
        return shiroFilterFactoryBean;
    }

    @Bean
    public FilterRegistrationBean<Filter> filterRegistrationBean(ShiroFilterFactoryBean shiroFilterFactoryBean) throws Exception {
        FilterRegistrationBean<Filter> filterRegistration = new FilterRegistrationBean<>();
        filterRegistration.setFilter((Filter) shiroFilterFactoryBean.getObject());
        filterRegistration.addInitParameter("targetFilterLifecycle", "true");
        filterRegistration.setAsyncSupported(true);
        filterRegistration.setEnabled(true);
        //这里添加一下对DispatcherType.ASYNC的支持就可以了
        filterRegistration.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ASYNC);
        return filterRegistration;
    }

    @Bean
    ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        return chainDefinition;
    }
}