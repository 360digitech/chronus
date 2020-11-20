package com.qihoo.finance.chronus.master.bo;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MutexMap {
    /**
     * 模式
     */
    public enum Mode {
        /**
         * 左斥右
         * 左节点需要等待右节点处理完成
         */
        LR,
        /**
         * 右斥左
         * 右节点需要等待左节点处理完成
         */
        RL
    }

    public MutexMap(Mode mode) {
        this.mutexNodeMap = new HashMap();
        this.mode = mode;
    }

    private Mode mode;
    private Map<String, MutexNode> mutexNodeMap;

    public boolean mutex(String key) {
        if (mutexNodeMap.isEmpty()) {
            return false;
        }
        for (MutexNode mutexNode : mutexNodeMap.values()) {
            if (Mode.LR.equals(mode) && key.equals(mutexNode.getLeftKey())) {
                return true;
            }
            if (Mode.RL.equals(mode) && key.equals(mutexNode.getRightKey())) {
                return true;
            }
        }
        return false;
    }

    public boolean containsRelationKey(String relationKey) {
        return mutexNodeMap.containsKey(relationKey);
    }

    public void remove(String key) {
        if (mutexNodeMap.isEmpty()) {
            return;
        }
        Iterator<MutexNode> iterator = mutexNodeMap.values().iterator();
        while (iterator.hasNext()) {
            MutexNode mutexNode = iterator.next();
            if (Mode.LR.equals(mode) && key.equals(mutexNode.getRightKey())) {
                iterator.remove();
            }
            if (Mode.RL.equals(mode) && key.equals(mutexNode.getLeftKey())) {
                iterator.remove();
            }
        }
    }

    public void put(String key1, String relationKey, String key2) {
        MutexNode mutexNode = new MutexNode();
        mutexNode.setLeftKey(key1);
        mutexNode.setRelationKey(relationKey);
        mutexNode.setRightKey(key2);
        mutexNodeMap.put(relationKey, mutexNode);
    }

    public void putLR(String key1, String relationKey) {
        MutexNode mutexNode = new MutexNode();
        mutexNode.setLeftKey(key1);
        mutexNode.setRelationKey(relationKey);
        if (mutexNodeMap.containsKey(relationKey)) {
            MutexNode tmpMutexNode = mutexNodeMap.get(relationKey);
            tmpMutexNode.setLeftKey(key1);
            mutexNodeMap.put(relationKey, tmpMutexNode);
            return;
        }
        mutexNodeMap.put(relationKey, mutexNode);
    }

    public void putRL(String key2, String relationKey) {
        MutexNode mutexNode = new MutexNode();
        mutexNode.setRelationKey(relationKey);
        mutexNode.setRightKey(key2);
        if (mutexNodeMap.containsKey(relationKey)) {
            MutexNode tmpMutexNode = mutexNodeMap.get(relationKey);
            tmpMutexNode.setRightKey(key2);
            mutexNodeMap.put(relationKey, tmpMutexNode);
        }
    }


    public void clear() {
        this.mutexNodeMap.clear();
    }
}
