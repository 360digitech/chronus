package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.task.dao.TaskItemDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.Date;
import java.util.List;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class TaskItemMongoDBDaoImpl extends AbstractMongoBaseDao<TaskItemEntity> implements TaskItemDao {

    public TaskItemMongoDBDaoImpl(String collectionName, @Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, collectionName);
    }

    @Override
    public TaskItemEntity getTaskItem(String taskItemId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskItemId").is(taskItemId));
        return super.selectOne(query);
    }

    @Override
    public List<TaskItemEntity> getTaskItemList(String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskName").is(taskName));
        return super.selectList(query);
    }

    @Override
    public void create(TaskItemEntity taskItemEntity) {
        super.insert(taskItemEntity);
    }

    @Override
    public void delete(TaskItemEntity taskItemEntity) {
        if (taskItemEntity.getId() != null) {
            super.deleteById(taskItemEntity.getId());
        }
    }


    @Override
    public void deleteByTaskItemInfo(TaskItemEntity taskItemEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskItemId").is(taskItemEntity.getTaskItemId()));
        query.addCriteria(Criteria.where("version").is(taskItemEntity.getVersion()));
        super.delete(query);
    }

    @Override
    public List<TaskItemEntity> selectAllTaskItem() {
        return selectListAll();
    }

    @Override
    public List<TaskItemEntity> selectTaskItemByCluster(String cluster) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        List<TaskItemEntity> resultList = super.selectList(query);
        return resultList;
    }

    @Override
    public void update(TaskItemEntity taskItemEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskItemId").is(taskItemEntity.getTaskItemId()));
        query.addCriteria(Criteria.where("version").is(taskItemEntity.getVersion()));
        Update update = new Update();
        update.set("version", taskItemEntity.getVersion() + 1);
        update.set("state", taskItemEntity.getState());
        update.set("message", taskItemEntity.getMessage());
        update.set("cluster", taskItemEntity.getCluster());
        update.set("dateUpdated", new Date());
        update.set("taskDateUpdated", taskItemEntity.getTaskDateUpdated());
        super.updateFirst(query, update);
    }

    @Override
    public boolean restartLock(TaskItemEntity taskItemEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("taskItemId").is(taskItemEntity.getTaskItemId()));
        query.addCriteria(Criteria.where("version").is(taskItemEntity.getVersion()));

        Update update = new Update();
        update.set("version", taskItemEntity.getVersion() + 1);
        update.set("state", taskItemEntity.getState());
        update.set("message", "任务锁定中,等待加载");
        update.set("cluster", taskItemEntity.getCluster());
        update.set("dateUpdated", new Date());
        update.set("taskDateUpdated", taskItemEntity.getTaskDateUpdated());
        update.set("masterAddress", taskItemEntity.getMasterAddress());
        update.set("masterVersion", taskItemEntity.getMasterVersion());
        update.set("workerAddress", taskItemEntity.getWorkerAddress());
        update.set("workerVersion", taskItemEntity.getWorkerVersion());
        update.set("taskItems", taskItemEntity.getTaskItems());
        boolean flag = super.updateFirst(query, update);
        if (flag) {
            taskItemEntity.setVersion(taskItemEntity.getVersion() + 1);
        }
        return flag;
    }
}
