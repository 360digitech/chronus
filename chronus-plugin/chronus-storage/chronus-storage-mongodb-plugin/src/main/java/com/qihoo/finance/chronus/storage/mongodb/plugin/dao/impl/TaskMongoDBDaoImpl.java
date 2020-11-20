package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageQueryParams;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class TaskMongoDBDaoImpl extends AbstractMongoBaseDao<TaskEntity> implements TaskDao {

    public TaskMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public void insert(TaskEntity taskEntity) {
        taskEntity.setId(null);
        taskEntity.setDateCreated(new Date());
        taskEntity.setDateUpdated(new Date());
        super.insert(taskEntity);
    }

    @Override
    public void update(TaskEntity taskEntity) {
        taskEntity.setDateUpdated(new Date());
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
    public TaskEntity selectTaskInfoByTaskName(String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskName").is(taskName));
        return selectOne(query);
    }

    @Override
    public PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity, List<String> dealSysCodes) {
        Integer page = taskEntity.getPageNum() - 1;
        Integer limit = taskEntity.getPageSize();
        PageQueryParams pageQueryParams = new PageQueryParams(page, limit);
        Query query = new Query();
        query.addCriteria(dealSysCodes.contains(taskEntity.getDealSysCode())
                ? Criteria.where("dealSysCode").is(taskEntity.getDealSysCode())
                : Criteria.where("dealSysCode").in(dealSysCodes));
        if (StringUtils.isNotEmpty(taskEntity.getTaskName())) {
            query.addCriteria(Criteria.where("taskName").regex(".*?" + taskEntity.getTaskName() + ".*", "i"));
        }
        if (StringUtils.isNotEmpty(taskEntity.getState())) {
            query.addCriteria(Criteria.where("state").is(taskEntity.getState()));
        }
        query.with(new Sort(Sort.Direction.DESC, "dateUpdated"));
        return generatePageResult(super.countByQuery(query), limit, page, super.selectByQuery(query, pageQueryParams));
    }


    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").in(cluster));
        query.with(new Sort(Sort.Direction.DESC, "dateUpdated"));
        return selectList(query);
    }


    @Override
    public List<TaskEntity> selectTaskInfoBySysCode(String dealSysCode) {
        Query query = new Query();
        query.addCriteria(Criteria.where("dealSysCode").in(dealSysCode));
        return selectList(query);
    }

    @Override
    public List<TaskEntity> selectAllTaskInfo() {
        return selectListAll();
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTaskNames(Collection<String> taskNameSet) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskName").in(taskNameSet));
        return selectList(query);
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTag(String tag) {
        Query query = new Query();
        query.addCriteria(Criteria.where("tag").is(tag));
        return selectList(query);
    }
}
