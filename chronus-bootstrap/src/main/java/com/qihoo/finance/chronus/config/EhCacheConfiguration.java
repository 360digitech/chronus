package com.qihoo.finance.chronus.config;

import com.qihoo.finance.chronus.common.ehcache.Ehcache;
import lombok.extern.slf4j.Slf4j;
import net.sf.ehcache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

/**
 * MsfSystemAutoConfiguration class
 *
 * @author xiongpu
 * @date 2019/06/29
 */
@Slf4j
@Configuration
public class EhCacheConfiguration {

    @Bean
    public EhCacheCacheManager ehCacheCacheManager(CacheManager cacheManager) {
        EhCacheCacheManager ehCacheCacheManager = new EhCacheCacheManager();
        ehCacheCacheManager.setCacheManager(cacheManager);
        log.debug("EhCacheCacheManager初始化完成");
        return ehCacheCacheManager;
    }

    @Bean
    public EhCacheManagerFactoryBean cacheManager() {
        ClassPathResource config = new ClassPathResource("/cache/ehcache-local.xml");
        EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
        ehCacheManagerFactoryBean.setConfigLocation(config);
        log.debug("EhCacheManagerFactoryBean初始化完成");
        return ehCacheManagerFactoryBean;
    }

    @Bean
    public Ehcache ehcache() {
        return new Ehcache();
    }

}
