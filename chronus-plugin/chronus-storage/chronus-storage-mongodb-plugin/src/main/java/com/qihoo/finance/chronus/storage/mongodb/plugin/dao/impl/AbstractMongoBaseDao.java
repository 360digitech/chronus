package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.Entity;
import com.qihoo.finance.chronus.metadata.api.common.PageQueryParams;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by xiongpu on 2017/10/2.
 */
public abstract class AbstractMongoBaseDao<T extends Entity> {

    private Class<T> entityClass;

    private String collectionName;

    private MongoTemplate mongotemplate;

    protected AbstractMongoBaseDao(MongoTemplate mongotemplate, String collectionName) {
        Type genType = getClass().getGenericSuperclass();
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();
        this.entityClass = (Class) params[0];
        this.collectionName = collectionName;
        this.mongotemplate = mongotemplate;
    }

    protected T selectOne(Query query) {
        return mongotemplate.findOne(query, entityClass, collectionName);
    }

    protected List<T> selectListByIds(Collection<Object> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        return mongotemplate.find(query, entityClass, collectionName);
    }

    protected List<T> selectList(Query query) {
        return mongotemplate.find(query, entityClass, collectionName);
    }

    protected List<T> selectListAll() {
        return mongotemplate.findAll(entityClass, collectionName);
    }

    protected void insert(T entity) {
        mongotemplate.insert(entity, collectionName);
    }

    protected void insert(Collection<T> entityList) {
        mongotemplate.insert(entityList, collectionName);
    }

    protected void insertOrUpdate(T entity) {
        mongotemplate.save(entity, collectionName);
    }

    protected void delete(Query query) {
        mongotemplate.remove(query, entityClass, collectionName);
    }

    protected void deleteById(Object id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        mongotemplate.remove(query, entityClass, collectionName);
    }

    /**
     * 提供实现但是不直接暴露方法
     *
     * @param ids
     */
    protected void deleteByByIds(Collection<Object> ids) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").in(ids));
        this.delete(query);
    }

    protected void updateById(Object id, Update update) {
        Query query = new Query();
        query.addCriteria(Criteria.where("id").is(id));
        this.updateFirst(query, update);
    }

    protected boolean updateFirst(Query query, Update update) {
       return mongotemplate.updateFirst(query, update, entityClass, collectionName).getModifiedCount() > 0;
    }

    protected void updateMulti(Query query, Update update) {
        mongotemplate.updateMulti(query, update, entityClass, collectionName);
    }

    protected List<T> selectByQuery(Query query, PageQueryParams queryParams) {
        Pageable pageable = PageRequest.of(queryParams.getPage(), queryParams.getLimit());
        query.with(pageable);
        List<T> list = mongotemplate.find(query, entityClass, collectionName);
        return list;
    }


    protected Long countByQuery(Query query) {
        Long count = mongotemplate.count(query, entityClass, collectionName);
        return count;
    }

    protected PageResult<T> findAllByPage(Integer page, Integer limit, Map<String, String> requestParams) {
        page = page - 1;
        PageQueryParams pageQueryParams = new PageQueryParams(page, limit);
        Map<String, String> params = new HashMap<>(8);
        params.putAll(requestParams);
        Query query = getWhereParamsByRequest(params);
        return generatePageResult(this.countByQuery(query), limit, page, this.selectByQuery(query, pageQueryParams));
    }


    protected PageResult<T> generatePageResult(Long count, Integer pageSize, Integer pageNum, List<T> list) {
        final PageResult<T> pageResult = new PageResult<>();
        pageResult.setTotal(count);
        pageResult.setPageSize(pageSize);
        pageResult.setPageNum(pageNum);
        pageResult.setList(list);
        return pageResult;
    }


    protected Query getWhereParamsByRequest(Map<String, String> queryParams) {
        Query query = new Query();
        for (Map.Entry<String, String> entry : queryParams.entrySet()) {
            if (entry.getValue() != null && StringUtils.isNotBlank(entry.getValue())) {
                Pattern pattern = Pattern.compile("^.*" + escapeExprSpecialWord(entry.getValue().toString()) + ".*$");
                query.addCriteria(Criteria.where(entry.getKey()).regex(pattern));
            }
        }
        return query;
    }

    private static String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};

    protected String escapeExprSpecialWord(String keyword) {
        if (StringUtils.isNotBlank(keyword)) {
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    protected MongoTemplate getMongoTemplate() {
        return mongotemplate;
    }

}
