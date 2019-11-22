package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;


import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
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
    public ClusterMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.CLUSTER_INFO);
    }

    @Override
    public void insert(ClusterEntity clusterEntity) {
        clusterEntity.setDateCreated(new Date());
        clusterEntity.setDateUpdated(new Date());
        super.insert(clusterEntity);
    }

    @Override
    public void updateDesc(ClusterEntity clusterEntity) {
        Update update = new Update();
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
}
