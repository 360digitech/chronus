package com.qihoo.finance.chronus.common;

import com.qihoo.finance.chronus.common.filter.ServiceContextFilter;
import com.qihoo.finance.chronus.common.interceptor.CsrfInterceptor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        InterceptorRegistration registration = registry.addInterceptor(new CsrfInterceptor());
        registration.excludePathPatterns("/api/login");
        registration.addPathPatterns("/api/**");
    }

    @Bean
    public FilterRegistrationBean serviceContextFilter(){
        FilterRegistrationBean<ServiceContextFilter> filterBean = new FilterRegistrationBean();
        filterBean.setFilter(new ServiceContextFilter());
        filterBean.setName("ServiceContextFilter");
        filterBean.addUrlPatterns("/api/*");
        return filterBean;
    }
}