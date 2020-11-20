package com.qihoo.finance.chronus.core.log;

import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.log.enums.NodeLogTypeEnum;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;
import lombok.ToString;
import org.apache.commons.lang3.time.StopWatch;

import java.util.Date;

/**
 * Created by xiongpu on 2019/11/2.
 */
@ToString
public class NodeLogBO {

    private StopWatch stopWatch = new StopWatch();

    private NodeLogEntity eventEntity = new NodeLogEntity();

    public static NodeLogBO start(NodeInfo currentNode, NodeLogTypeEnum eventEnum) {
        NodeLogBO event = new NodeLogBO();
        NodeLogEntity eventEntity = event.eventEntity;
        eventEntity.setCluster(currentNode.getCluster());
        eventEntity.setAddress(currentNode.getAddress());
        eventEntity.setVersion(currentNode.getVersion());
        eventEntity.setCode(eventEnum.getCode());
        eventEntity.setDesc(eventEnum.getDesc());
        eventEntity.setDateCreated(new Date());
        event.stopWatch.start();
        return event;
    }

    public NodeLogBO stop(String message) {
        stopWatch.stop();
        this.eventEntity.setCostTime(stopWatch.getTime());
        this.eventEntity.setMessage(message);
        return this;
    }


    public NodeLogBO stop(String message, String content) {
        stopWatch.stop();
        this.eventEntity.setContent(content);
        this.eventEntity.setCostTime(stopWatch.getTime());
        this.eventEntity.setMessage(message);
        return this;
    }


    public NodeLogEntity getEventEntity() {
        return eventEntity;
    }
}
