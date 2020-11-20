package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.group.dto.GroupDao;
import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class GroupMongoDBDaoImpl extends AbstractMongoBaseDao<GroupEntity> implements GroupDao {

    public GroupMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongotemplate) {
        super(mongotemplate, collectionName);
    }

    @Override
    public void insert(GroupEntity groupEntity) {
        super.insert(groupEntity);
    }

    @Override
    public void update(GroupEntity groupEntity) {
        super.insertOrUpdate(groupEntity);
    }

    @Override
    public void deleteByGroupName(String groupName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupName").is(groupName));
        super.delete(query);
    }

    @Override
    public List<GroupEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public GroupEntity selectByName(String name) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupName").is(name));
        return super.selectOne(query);
    }
}
