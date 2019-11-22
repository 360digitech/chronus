package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;


import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.event.dao.EventDao;
import com.qihoo.finance.chronus.metadata.api.event.entity.EventEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
public class EventMongoDBDaoImpl extends AbstractMongoBaseDao<EventEntity> implements EventDao {
    public EventMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.EVENT_INFO);
    }

    @Override
    public void insert(EventEntity eventEntity) {
        if (eventEntity.getDateCreated() == null) {
            eventEntity.setDateCreated(new Date());
        }
        super.insert(eventEntity);
    }

    @Override
    public void updateDuplicateEvent(EventEntity eventEntity, Object lastId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("address").is(eventEntity.getAddress()));
        query.addCriteria(Criteria.where("cluster").is(eventEntity.getCluster()));
        query.addCriteria(Criteria.where("version").is(eventEntity.getVersion()));

        Update update = new Update();
        update.set("message", eventEntity.getMessage());
        update.set("content", eventEntity.getContent());
        update.set("costTime", eventEntity.getCostTime());
        update.set("dateCreated", eventEntity.getDateCreated());
        super.updateById(lastId, update);
    }

    @Override
    public List<EventEntity> getLastEvent(String cluster, String address, String version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("address").is(address));
        query.addCriteria(Criteria.where("version").is(version));
        query.with(new Sort(Sort.Direction.DESC, "dateCreated"));
        query.limit(3);
        return super.selectList(query);
    }

    @Override
    public List<EventEntity> getAllEvent(String cluster, String address, String version) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("address").is(address));
        query.addCriteria(Criteria.where("version").is(version));
        query.with(new Sort(Sort.Direction.DESC, "dateCreated"));
        return super.selectList(query);
    }
}
