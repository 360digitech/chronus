package com.qihoo.finance.chronus.common.spi.autoconfigure;

import com.qihoo.finance.chronus.common.spi.LogoutHandler;
import com.qihoo.finance.chronus.common.spi.UserService;
import com.qihoo.finance.chronus.common.spi.defaultimpl.DefaultLogoutHandler;
import com.qihoo.finance.chronus.common.spi.defaultimpl.DefaultShiroRealm;
import com.qihoo.finance.chronus.common.spi.defaultimpl.DefaultUserService;
import com.qihoo.finance.chronus.common.spi.ldap.LdapLogoutHandler;
import com.qihoo.finance.chronus.common.spi.ldap.LdapRealm;
import com.qihoo.finance.chronus.common.spi.ldap.LdapUserService;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.spring.web.config.DefaultShiroFilterChainDefinition;
import org.apache.shiro.spring.web.config.ShiroFilterChainDefinition;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author chenxiyu
 * @date 2019/12/10
 */
@Configuration
@ConditionalOnProperty(name = "chronus.auth")
public class ChronusShiroAutoConfiguration {


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
    @Order(99)
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, Environment env) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        // 必须设置SecuritManager
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        // 未登录状态处理
//        filters.put("authc", new ShiroLoginFilter());
        shiroFilterFactoryBean.setFilters(filters);

        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl(env.getProperty("shiro.loginUrl"));
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl(env.getProperty("shiro.successUrl"));
        // 未授权界面;
        // shiroFilterFactoryBean.setUnauthorizedUrl(env.getProperty("shiro.unauthorizedUrl"));
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

        filterChainDefinitionMap.put("/api/login", "anon");
        filterChainDefinitionMap.put("/api/logout", "anon");
        filterChainDefinitionMap.put("/api/monitor/dataGuard", "anon");
        //filterChainDefinitionMap.put("/api/**", "tokenFilter");
        //过滤链定义，从上向下顺序执行，一般将/**放在最为下边
        filterChainDefinitionMap.put("/api/**", "authc");
        filterChainDefinitionMap.put("/", "authc");
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

    /**
     * Shiro生命周期处理器
     */
    @Bean(name = "lifecycleBeanPostProcessor")
    public static LifecycleBeanPostProcessor getLifecycleBeanPostProcessor() {
        return new LifecycleBeanPostProcessor();
    }

    /**
     * 开启Shiro的注解
     */
    @Bean
    @DependsOn("lifecycleBeanPostProcessor")
    public DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator creator = new DefaultAdvisorAutoProxyCreator();
        creator.setProxyTargetClass(true);
        return creator;
    }

    /**
     * 开启shiro aop注解支持.
     */
    @Bean
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(SecurityManager securityManager) {
        AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor = new AuthorizationAttributeSourceAdvisor();
        authorizationAttributeSourceAdvisor.setSecurityManager(securityManager);
        return authorizationAttributeSourceAdvisor;
    }

    @ConditionalOnProperty(name = "chronus.auth", havingValue = "ldap", matchIfMissing = false)
    static class LdapConfiguration {

        @Bean
        @ConditionalOnMissingBean(LogoutHandler.class)
        public LdapLogoutHandler ldapLogoutHandler() {
            return new LdapLogoutHandler();
        }

        @Bean
        @ConditionalOnMissingBean(UserService.class)
        public LdapUserService ldapUserService() {
            return new LdapUserService();
        }

        @Bean
        public LdapRealm ldapRealm() {
            return new LdapRealm();
        }

        @Bean
        public DefaultWebSecurityManager securityManager(LdapRealm ldapRealm, DefaultWebSessionManager sessionManager) {
            DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
            manager.setRealm(ldapRealm);
            manager.setSessionManager(sessionManager);
            return manager;
        }
    }


    @ConditionalOnProperty(name = "chronus.auth", havingValue = "default", matchIfMissing = true)
    static class DefaultImplConfiguration {


        @Bean
        @ConditionalOnMissingBean(LogoutHandler.class)
        public DefaultLogoutHandler defaultLogoutHandler() {
            return new DefaultLogoutHandler();
        }

        @Bean
        @ConditionalOnMissingBean(UserService.class)
        public DefaultUserService defaultUserService() {
            return new DefaultUserService();
        }

        @Bean
        public DefaultShiroRealm defaultRealm() {
            return new DefaultShiroRealm();
        }

        @Bean
        public DefaultWebSecurityManager securityManager(DefaultShiroRealm defaultShiroRealm, DefaultWebSessionManager sessionManager) {
            DefaultWebSecurityManager manager = new DefaultWebSecurityManager();
            manager.setRealm(defaultShiroRealm);
            manager.setSessionManager(sessionManager);
            return manager;
        }
    }
}