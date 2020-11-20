package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;


import com.qihoo.finance.chronus.metadata.api.log.dao.NodeLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Date;
import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
public class NodeLogMongoDBDaoImpl extends AbstractMongoBaseDao<NodeLogEntity> implements NodeLogDao {
    public NodeLogMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public void insert(NodeLogEntity nodeLogEntity) {
        if (nodeLogEntity.getDateCreated() == null) {
            nodeLogEntity.setDateCreated(new Date());
        }
        super.insert(nodeLogEntity);
    }


    @Override
    public List<NodeLogEntity> selectListByClusterAndAddress(String cluster, String address) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("address").is(address));
        query.with(new Sort(Sort.Direction.DESC, "dateCreated"));
        return selectList(query);
    }
}
