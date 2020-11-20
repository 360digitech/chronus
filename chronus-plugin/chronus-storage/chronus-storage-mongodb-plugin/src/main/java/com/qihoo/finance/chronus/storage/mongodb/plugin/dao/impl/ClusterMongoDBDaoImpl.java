package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;


import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.metadata.api.common.enums.ClusterStateEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
public class ClusterMongoDBDaoImpl extends AbstractMongoBaseDao<ClusterEntity> implements ClusterDao {
    public ClusterMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public void insert(ClusterEntity clusterEntity) {
        clusterEntity.setDateCreated(new Date());
        clusterEntity.setDateUpdated(new Date());
        super.insert(clusterEntity);
    }

    @Override
    public void update(ClusterEntity clusterEntity) {
        Update update = new Update();
        update.set("clusterState", clusterEntity.getClusterState());
        update.set("clusterDesc", clusterEntity.getClusterDesc());
        update.set("dateUpdated", new Date());
        if (StringUtils.isNotBlank(clusterEntity.getUpdatedBy())) {
            update.set("updatedBy", clusterEntity.getUpdatedBy());
        }
        super.updateById(clusterEntity.getId(), update);
    }

    @Override
    public void delete(String cluster) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        super.delete(query);
    }

    @Override
    public List<ClusterEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public ClusterEntity selectByCluster(String cluster) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        return super.selectOne(query);
    }

    @Override
    public void dataGuardStartTrx(String dataGuardCluster) {
        {
            Query query = new Query();
            query.addCriteria(Criteria.where("cluster").is(dataGuardCluster));
            Update update = new Update();
            update.set("clusterState", ClusterStateEnum.DATA_GUARD.getState());
            super.updateMulti(query, update);
        }

        {
            Query query = new Query();
            query.addCriteria(Criteria.where("cluster").ne(dataGuardCluster));
            Update update = new Update();
            update.set("clusterState", ClusterStateEnum.CLOSE.getState());
            super.updateMulti(query, update);
        }
    }

    @Override
    public void dataGuardStop(String dataGuardCluster) {
        Query query2 = new Query();
        query2.addCriteria(Criteria.where("cluster").ne(""));

        Update update2 = new Update();
        update2.set("clusterState", ClusterStateEnum.NORMAL.getState());
        super.updateMulti(query2, update2);
    }
}
