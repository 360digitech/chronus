package com.qihoo.finance.chronus.config;

import com.qihoo.finance.chronus.common.ehcache.ChronusEhcache;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * @author xiongpu
 * @date 2019/06/29
 */
@Slf4j
@Configuration
public class EhCacheConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public EhCacheCacheManager cacheManager(CacheManager ehCacheManager) {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(ehCacheManager);
        log.debug("EhCacheCacheManager初始化完成");
        return ehCacheCacheManager;
    }

    @Bean
    @ConditionalOnMissingBean
    public EhCacheManagerFactoryBean ehCacheManager() {
        ClassPathResource config = new ClassPathResource("/cache/ehcache-local.xml");
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(config);
        log.debug("EhCacheManagerFactoryBean初始化完成");
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public ChronusEhcache chronusEhcache() {
        return new ChronusEhcache();
    }
}
