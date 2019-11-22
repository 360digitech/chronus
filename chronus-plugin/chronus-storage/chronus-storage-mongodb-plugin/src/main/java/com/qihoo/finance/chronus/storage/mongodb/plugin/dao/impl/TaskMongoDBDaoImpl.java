package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class TaskMongoDBDaoImpl extends AbstractMongoBaseDao<TaskEntity> implements TaskDao {

    public TaskMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.TASK_INFO);
    }

    @Override
    public void insert(TaskEntity taskEntity) {
        super.insert(taskEntity);
    }


    @Override
    public void update(TaskEntity taskEntity) {
        super.insertOrUpdate(taskEntity);
    }

    @Override
    public void delete(String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskName").is(taskName));
        super.delete(query);
    }

    @Override
    public TaskEntity selectById(String id) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(id));
        return super.selectOne(query);
    }

    @Override
    public TaskEntity selectTaskInfoByTaskName(String cluster, String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("taskName").is(taskName));
        return selectOne(query);
    }

    @Override
    public List<TaskEntity> selectListAll() {
        return super.selectListAll();
    }

    @Override
    public PageResult<TaskEntity> selectListByPage(Integer page, Integer limit, Map<String, String> param) {
        return super.findAllByPage(page, limit, param);
    }


    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.with(new Sort(Sort.Direction.DESC, "dateUpdated"));
        return selectList(query);
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTag(String tag) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tag").is(tag));
        return selectList(query);
    }
}
