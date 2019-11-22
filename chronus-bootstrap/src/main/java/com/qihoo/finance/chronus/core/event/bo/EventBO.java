package com.qihoo.finance.chronus.core.event.bo;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;
import lombok.ToString;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Date;

/**
 * Created by xiongpu on 2019/11/2.
 */
@ToString
public class EventBO {

    private StopWatch stopWatch = new StopWatch();

    private EventEntity eventEntity = new EventEntity();

    public static EventBO start(NodeInfo currentNode, EventEnum eventEnum) {
        EventBO event = new EventBO();
        EventEntity eventEntity = event.eventEntity;
        eventEntity.setCluster(currentNode.getCluster());
        eventEntity.setAddress(currentNode.getAddress());
        eventEntity.setVersion(currentNode.getVersion());
        eventEntity.setCode(eventEnum.getCode());
        eventEntity.setDesc(eventEnum.getDesc());
        eventEntity.setDateCreated(new Date());
        event.stopWatch.start();
        return event;
    }

    public EventBO stop(String message) {
        stopWatch.stop();
        this.eventEntity.setCostTime(stopWatch.getTime());
        this.eventEntity.setMessage(message);
        return this;
    }


    public EventBO stop(String message, String content) {
        stopWatch.stop();
        this.eventEntity.setContent(content);
        this.eventEntity.setCostTime(stopWatch.getTime());
        this.eventEntity.setMessage(message);
        return this;
    }


    public EventEntity getEventEntity() {
        return eventEntity;
    }
}
