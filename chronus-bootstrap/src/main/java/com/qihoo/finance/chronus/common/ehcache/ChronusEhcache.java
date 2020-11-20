package com.qihoo.finance.chronus.common.ehcache;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCacheManager;

import javax.annotation.Resource;

import static com.qihoo.finance.chronus.common.ehcache.CacheConstant.EHCACHE_NAME_FOREVER;

/**
 * Created by huangjianting-ira on 2016/4/12.
 */

public class ChronusEhcache {
    public ChronusEhcache() {

    }

    public ChronusEhcache(EhCacheCacheManager ehCacheCacheManager) {
        this.ehCacheCacheManager = ehCacheCacheManager;
    }

    @Resource
    private EhCacheCacheManager ehCacheCacheManager;

    /**
     * 获取SYS_CACHE缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        return get(EHCACHE_NAME_FOREVER, key);
    }

    /**
     * 写入SYS_CACHE缓存
     *
     * @param key
     * @return
     */
    public void put(String key, Object value) {
        put(EHCACHE_NAME_FOREVER, key, value);
    }

    /**
     * 从SYS_CACHE缓存中移除
     *
     * @param key
     * @return
     */
    public void remove(String key) {
        remove(EHCACHE_NAME_FOREVER, key);
    }

    /**
     * 获取缓存
     *
     * @param cacheName
     * @param key
     * @return
     */
    public Object get(String cacheName, String key) {
        Cache.ValueWrapper simpleValueWrapper = getCache(cacheName).get(key);
        return simpleValueWrapper == null ? null : simpleValueWrapper.get();
    }

    /**
     * 写入缓存
     *
     * @param cacheName
     * @param key
     * @param value
     */
    public void put(String cacheName, String key, Object value) {
        getCache(cacheName).put(key, value);
    }

    /**
     * 从缓存中移除
     *
     * @param cacheName
     * @param key
     */
    public void remove(String cacheName, String key) {
        getCache(cacheName).evict(key);
    }

    /**
     * 获得一个Cache
     *
     * @param cacheName
     * @return
     */
    public Cache getCache(String cacheName) {
        Cache cache = ehCacheCacheManager.getCache(cacheName);
        if (cache == null) {
            cache = getCacheWithCreate(cacheName);
        }
        return cache;
    }

    /**
     * 获得一个Cache，没有则创建一个。
     *
     * @param cacheName
     * @return
     */
    private synchronized Cache getCacheWithCreate(String cacheName) {
        Cache cache = ehCacheCacheManager.getCache(cacheName);
        if (cache == null) {
            ehCacheCacheManager.getCacheManager().addCache(cacheName);
            cache = ehCacheCacheManager.getCache(cacheName);
        }
        return cache;
    }

}
