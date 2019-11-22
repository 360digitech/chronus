package com.qihoo.finance.chronus.core.event.service.impl;

import com.qihoo.finance.chronus.core.event.bo.EventBO;
import com.qihoo.finance.chronus.core.event.service.EventService;
import com.qihoo.finance.chronus.metadata.api.event.dao.EventDao;
import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2019/11/2.
 */
@Slf4j
public class EventServiceImpl implements EventService {

    private static final int DUPLICATE_EVENT_COUNT = 3;

    @Resource
    private EventDao eventDao;

    @Override
    public void submitEvent(EventBO event) {
        EventEntity eventEntity = event.getEventEntity();
        try {
            List<EventEntity> lastEvent = eventDao.getLastEvent(eventEntity.getCluster(), eventEntity.getAddress(), eventEntity.getVersion());
            if (CollectionUtils.size(lastEvent) >= DUPLICATE_EVENT_COUNT) {
                Set<String> eventCodes = lastEvent.stream().map(EventEntity::getCode).collect(Collectors.toSet());
                if (CollectionUtils.size(eventCodes) == 1 && eventCodes.contains(eventEntity.getCode())) {
                    eventDao.updateDuplicateEvent(eventEntity, lastEvent.get(0).getId());
                    return;
                }
            }
            eventDao.insert(eventEntity);
        } catch (Exception e) {
            log.error("保存事件异常 eventEntity:{}", eventEntity, e);
        }
    }

    @Override
    public List<EventEntity> getAllEvent(String cluster, String address, String version) {
        return eventDao.getAllEvent(cluster, address, version);
    }
}
