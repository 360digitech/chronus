package com.qihoo.finance.chronus.metadata.api.event.dao;

import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/11/2.
 */
public interface EventDao {
    void insert(EventEntity eventEntity);

    /**
     * 替换最后一个重复的事件内容
     * @param eventEntity
     * @param lastId
     */
    void updateDuplicateEvent(EventEntity eventEntity,Object lastId);

    List<EventEntity> getLastEvent(String cluster,String address, String version);
    List<EventEntity> getAllEvent(String cluster,String address, String version);

}
