package com.qihoo.finance.chronus.core.event.service;

import com.qihoo.finance.chronus.core.event.bo.EventBO;
import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/11/2.
 */
public interface EventService {
    /**
     * 提交事件到数据库
     * @param event
     */
    void submitEvent(EventBO event);

    List<EventEntity> getAllEvent(String cluster,String address, String version);
}
