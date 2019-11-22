package com.qihoo.finance.chronus.storage.mongodb.plugin.config;

import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.event.dao.EventDao;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskRuntimeDao;
import com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by xiongpu on 2019/9/13.
 */
@Configuration
@ConditionalOnProperty(prefix = "chronus.plugin", name = "storage", havingValue = "mongodb")
public class MongoDBDaoConfiguration {

    @Bean("clusterDao")
    public ClusterDao clusterDao(MongoTemplate mongoTemplate) {
        ClusterDao clusterDao = new ClusterMongoDBDaoImpl(mongoTemplate);
        return clusterDao;
    }

    @Bean("tagDao")
    public TagDao tagDao(MongoTemplate mongoTemplate) {
        TagDao tagDao = new TagMongoDBDaoImpl(mongoTemplate);
        return tagDao;
    }

    @Bean("taskDao")
    public TaskDao taskDao(MongoTemplate mongoTemplate) {
        TaskDao taskDao = new TaskMongoDBDaoImpl(mongoTemplate);
        return taskDao;
    }

    @Bean("taskRuntimeDao")
    public TaskRuntimeDao taskRuntimeDao(MongoTemplate mongoTemplate) {
        TaskRuntimeDao taskRuntimeDao = new TaskRuntimeMongoDBDaoImpl(mongoTemplate);
        return taskRuntimeDao;
    }

    @Bean("systemGroupDao")
    public SystemGroupDao systemGroupDao(MongoTemplate mongoTemplate) {
        SystemGroupDao systemGroupDao = new SystemGroupMongoDBDaoImpl(mongoTemplate);
        return systemGroupDao;
    }

    @Bean("jobExecLogDao")
    public JobExecLogDao jobExecLogDao(MongoTemplate mongoTemplate) {
        JobExecLogDao jobExecLogDao = new JobExecLogMongoDBDaoImpl(mongoTemplate);
        return jobExecLogDao;
    }

    @Bean("eventDao")
    public EventDao eventDao(MongoTemplate mongoTemplate) {
        EventDao eventDao = new EventMongoDBDaoImpl(mongoTemplate);
        return eventDao;
    }

}
