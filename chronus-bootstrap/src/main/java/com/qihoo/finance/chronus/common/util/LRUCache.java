package com.qihoo.finance.chronus.common.util;


import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiongpu on 2017/6/29.
 */
public class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private static final long serialVersionUID = -5167631809472116969L;
    private volatile int maxCapacity;
    private final Lock lock;

    public LRUCache() {
        this(1000);
    }

    public LRUCache(int maxCapacity) {
        super(16, 0.75F, true);
        this.lock = new ReentrantLock();
        this.maxCapacity = maxCapacity;
    }

    @Override
    protected boolean removeEldestEntry(Entry<K, V> eldest) {
        return this.size() > this.maxCapacity;
    }

    @Override
    public boolean containsKey(Object key) {
        boolean var2;
        try {
            this.lock.lock();
            var2 = super.containsKey(key);
        } finally {
            this.lock.unlock();
        }

        return var2;
    }

    @Override
    public V get(Object key) {
        V var2;
        try {
            this.lock.lock();
            var2 = super.get(key);
        } finally {
            this.lock.unlock();
        }

        return var2;
    }

    @Override
    public V put(K key, V value) {
        V var3;
        try {
            this.lock.lock();
            var3 = super.put(key, value);
        } finally {
            this.lock.unlock();
        }

        return var3;
    }

    @Override
    public V remove(Object key) {
        V var2;
        try {
            this.lock.lock();
            var2 = super.remove(key);
        } finally {
            this.lock.unlock();
        }

        return var2;
    }

    @Override
    public int size() {
        int var1;
        try {
            this.lock.lock();
            var1 = super.size();
        } finally {
            this.lock.unlock();
        }

        return var1;
    }

    @Override
    public void clear() {
        try {
            this.lock.lock();
            super.clear();
        } finally {
            this.lock.unlock();
        }

    }

    public int getMaxCapacity() {
        return this.maxCapacity;
    }

    public void setMaxCapacity(int maxCapacity) {
        this.maxCapacity = maxCapacity;
    }
}
