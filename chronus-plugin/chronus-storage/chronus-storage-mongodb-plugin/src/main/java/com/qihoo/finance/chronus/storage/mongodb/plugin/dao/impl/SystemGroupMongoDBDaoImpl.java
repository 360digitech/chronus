package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by xiongpu on 2019/9/13.
 */
public class SystemGroupMongoDBDaoImpl extends AbstractMongoBaseDao<SystemGroupEntity> implements SystemGroupDao {

    public SystemGroupMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.SYSTEM_GROUP_INFO);
    }

    @Override
    public void insert(SystemGroupEntity systemGroupEntity) {
        super.insert(systemGroupEntity);
    }

    @Override
    public void update(SystemGroupEntity systemGroupEntity) {
        super.insertOrUpdate(systemGroupEntity);
    }

    @Override
    public void delete(Object id) {
        super.deleteById(id);
    }

    @Override
    public SystemGroupEntity selectByGroupName(String groupName, String sysCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("groupName").is(groupName));
        query.addCriteria(Criteria.where("sysCode").is(sysCode));
        SystemGroupEntity systemGroupEntity = super.selectOne(query);
        return systemGroupEntity;
    }

    @Override
    public List<SystemGroupEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public SystemGroupEntity loadSystemGroupBySysCode(String sysCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("sysCode").is(sysCode));
        SystemGroupEntity systemGroup = super.selectOne(query);
        return systemGroup;
    }

    @Override
    public PageResult<SystemGroupEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
        return super.findAllByPage(page,limit,param);
    }

}
