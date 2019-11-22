package com.qihoo.finance.chronus.common.ehcache;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * 本地缓存加载器
 * 作用：应用启动后，初始化内存；结合Spring task，定时刷新缓存;AB切换机制，刷新不影响缓存使用。
 * 避免缓存被击穿
 * Created by zhaoliping on 2016/6/8.
 */
public abstract class AbstractLocalCacheLoader implements ApplicationListener<ContextRefreshedEvent> {

    protected String switchA = "";
    protected String switchB = "";
    protected String current = "";
    @Autowired
    protected Ehcache ehcache;

    public AbstractLocalCacheLoader() {
        switchA = getCacheName() + "_A";
        switchB = getCacheName() + "_B";
        current = switchA;
    }

    /**
     * 自定义缓存定制，初始化数据
     * @return 缓存字典
     */
    public abstract Map<String, Object> init();

    /**
     * 自定义缓存定制，指定缓存名字
     *
     * @return
     */
    protected abstract String getCacheName();


    /**
     * 缓存刷新
     */
    public synchronized void update() {
        //缓存AB切换
        change();

        //加载数据
        Map<String, Object> datas = init();
        if (datas != null && !datas.isEmpty()) {
            for (Map.Entry<String, Object> item : datas.entrySet()) {
                put(item.getKey(), item.getValue());
            }
        }

        //缓存切换
        current = switchA;
        //老缓存清理
        Cache cache = ehcache.getCache(switchB);
        if (cache != null) {
            cache.clear();
        }
    }

    /**
     * 增加缓存值
     *
     * @param key
     * @param value
     */
    protected void put(String key, Object value) {
        ehcache.put(switchA, key, value);
    }

    /**
     * 取缓存值
     *
     * @param key
     * @return
     */
    public <T> T get(String key) {
        return (T) ehcache.get(current, key);
    }

    /**
     * AB切换
     */
    private void change() {
        String temp = switchA;
        switchA = switchB;
        switchB = temp;
    }

    /**
     * 在spring容器加载完成后，执行cache初始化
     *
     * @param contextRefreshedEvent
     */
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        update();
    }
}
