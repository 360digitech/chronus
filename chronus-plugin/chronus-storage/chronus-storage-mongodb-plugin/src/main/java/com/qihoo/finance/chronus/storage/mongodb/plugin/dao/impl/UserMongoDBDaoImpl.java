package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageQueryParams;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.user.dao.UserDao;
import com.qihoo.finance.chronus.metadata.api.user.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class UserMongoDBDaoImpl extends AbstractMongoBaseDao<UserEntity> implements UserDao {

    public UserMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }


    @Override
    public void insert(UserEntity userEntity) {
        super.insert(userEntity);
    }

    @Override
    public void update(UserEntity userEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userNo").is(userEntity.getUserNo()));
        Update update = new Update();
        update.set("name", userEntity.getName());
        update.set("group", userEntity.getGroup());
        update.set("roleNo", userEntity.getRoleNo());
        update.set("email", userEntity.getEmail());
        update.set("state", userEntity.getState());
        super.updateFirst(query, update);
    }

    @Override
    public void delete(String userNo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userNo").is(userNo));
        super.delete(query);
    }

    @Override
    public UserEntity selectUserInfoByUserNo(String userNo,String password) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userNo").is(userNo));
        query.addCriteria(Criteria.where("pwd").is(password));
        query.addCriteria(Criteria.where("state").is("Y"));
        return super.selectOne(query);
    }

    @Override
    public UserEntity selectUserInfoByUserNo(String userNo) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userNo").is(userNo));
        query.addCriteria(Criteria.where("state").is("Y"));
        return super.selectOne(query);
    }

    @Override
    public PageResult<UserEntity> selectListByPage(Integer page, Integer limit, UserEntity userEntity) {
        page = page - 1;
        PageQueryParams pageQueryParams = new PageQueryParams(page, limit);
        Query query = new Query();
        if(StringUtils.isNotEmpty(userEntity.getUserNo())){
            query.addCriteria(Criteria.where("userNo").is(userEntity.getUserNo()));
        }
        if(StringUtils.isNotEmpty(userEntity.getGroup())){
            query.addCriteria(Criteria.where("group").is(userEntity.getGroup()));
        }
        if(StringUtils.isNotEmpty(userEntity.getRoleNo())){
            query.addCriteria(Criteria.where("roleNo").is(userEntity.getRoleNo()));
        }
        query.with(new Sort(Sort.Direction.DESC, "dateUpdated"));
        return generatePageResult(super.countByQuery(query),limit,page,super.selectByQuery(query, pageQueryParams));
    }
}
