package com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskRuntimeDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * Created by xiongpu on 2019/8/10.
 */
@Slf4j
public class TaskRuntimeMongoDBDaoImpl extends AbstractMongoBaseDao<TaskRuntimeEntity> implements TaskRuntimeDao {

    public TaskRuntimeMongoDBDaoImpl(@Autowired MongoTemplate mongoTemplate) {
        super(mongoTemplate, TableConstant.TASK_RUNTIME_INFO);
    }

    @Override
    public void insert(TaskRuntimeEntity taskRuntimeEntity) {
        super.insert(taskRuntimeEntity);
    }

    @Override
    public void batchInsert(List<TaskRuntimeEntity> taskRuntimeEntityInitList) {
        super.insert(taskRuntimeEntityInitList);
    }

    @Override
    public List<TaskRuntimeEntity> selectTaskRuntimeByTaskName(String cluster, String taskName) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(cluster));
        query.addCriteria(Criteria.where("taskName").is(taskName));
        return super.selectList(query);
    }

    @Override
    public TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(taskRuntimeEntity.getCluster()));
        query.addCriteria(Criteria.where("taskName").is(taskRuntimeEntity.getTaskName()));
        query.addCriteria(Criteria.where("seqNo").is(taskRuntimeEntity.getSeqNo()));
        return super.selectOne(query);
    }

    @Override
    public void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity) {
        Query query = new Query();
        query.addCriteria(Criteria.where("cluster").is(taskRuntimeEntity.getCluster()));
        query.addCriteria(Criteria.where("taskName").is(taskRuntimeEntity.getTaskName()));
        query.addCriteria(Criteria.where("seqNo").is(taskRuntimeEntity.getSeqNo()));
        Update update = new Update();
        update.set("address", taskRuntimeEntity.getAddress());
        update.set("hostName", taskRuntimeEntity.getHostName());
        update.set("heartBeatTime", taskRuntimeEntity.getHeartBeatTime());
        update.set("registerTime", taskRuntimeEntity.getRegisterTime());
        update.set("nextRunStartTime", taskRuntimeEntity.getNextRunStartTime());
        update.set("nextRunEndTime", taskRuntimeEntity.getNextRunEndTime());
        update.set("lastFetchDataTime", taskRuntimeEntity.getLastFetchDataTime());
        update.set("state", taskRuntimeEntity.getState());
        update.set("taskItems", taskRuntimeEntity.getTaskItems());
        update.set("message", taskRuntimeEntity.getMessage());
        super.updateFirst(query, update);
    }

    @Override
    public void delete(TaskRuntimeEntity taskRuntimeEntity) {
        if (taskRuntimeEntity.getId() != null) {
            super.deleteById(taskRuntimeEntity.getId());
        }
    }
}
