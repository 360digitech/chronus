package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class TagMongoDBDaoImpl extends AbstractMongoBaseDao<TagEntity> implements TagDao {

    public TagMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public void insert(TagEntity tagEntity) {
        super.insert(tagEntity);
    }

    @Override
    public List<TagEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public void delete(String tagName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tag").is(tagName));
        super.delete(query);
    }

    @Override
    public void update(TagEntity tagEntity) {
        Update update = new Update();
        update.set("remark", tagEntity.getRemark());
        update.set("dateUpdated", new Date());
        update.set("updatedBy", tagEntity.getUpdatedBy());
        super.updateById(tagEntity.getId(), update);
    }

    @Override
    public TagEntity selectByTagName(String tag) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tag").is(tag));
        return super.selectOne(query);
    }

    @Override
    public PageResult<TagEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
        return super.findAllByPage(page, limit, param);
    }
}
