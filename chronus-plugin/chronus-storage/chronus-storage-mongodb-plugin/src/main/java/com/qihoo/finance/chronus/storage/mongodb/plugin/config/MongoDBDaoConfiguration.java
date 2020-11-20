package com.qihoo.finance.chronus.storage.mongodb.plugin.config;

import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import com.qihoo.finance.chronus.metadata.api.group.dto.GroupDao;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.dao.NodeLogDao;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskItemDao;
import com.qihoo.finance.chronus.metadata.api.user.dao.UserDao;
import com.qihoo.finance.chronus.storage.mongodb.plugin.dao.impl.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Created by xiongpu on 2019/9/13.
 */
@Configuration
@ConditionalOnProperty(prefix = "chronus.plugin", name = "storage", havingValue = "mongodb")
public class MongoDBDaoConfiguration {

    @Value("${chronus.plugin.storage.mongodb.collectionNamePrefix:}")
    private String collectionNamePrefix;

    private String genCollectionName(String prefix, String collectionName) {
        if (StringUtils.isBlank(prefix)) {
            return collectionName;
        }
        return prefix + "." + collectionName;
    }

    @Bean("clusterDao")
    public ClusterDao clusterDao(MongoTemplate mongoTemplate, Environment environment) {
        return new ClusterMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.clusterInfo", TableConstant.CLUSTER_INFO)), mongoTemplate);
    }

    @Bean("tagDao")
    public TagDao tagDao(MongoTemplate mongoTemplate, Environment environment) {
        return new TagMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.tagInfo", TableConstant.TAG_INFO)), mongoTemplate);
    }

    @Bean("taskDao")
    public TaskDao taskDao(MongoTemplate mongoTemplate, Environment environment) {
        return new TaskMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.taskInfo", TableConstant.TASK_INFO)), mongoTemplate);
    }

    @Bean("taskItemDao")
    public TaskItemDao taskItemDao(MongoTemplate mongoTemplate, Environment environment) {
        return new TaskItemMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.taskItemInfo", TableConstant.TASK_ITEM_INFO)), mongoTemplate);
    }

    @Bean("systemGroupDao")
    public SystemGroupDao systemGroupDao(MongoTemplate mongoTemplate, Environment environment) {
        return new SystemGroupMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.systemGroupInfo", TableConstant.SYSTEM_GROUP_INFO)), mongoTemplate);
    }

    @Bean("jobExecLogDao")
    public JobExecLogDao jobExecLogDao(MongoTemplate mongoTemplate, Environment environment) {
        return new JobExecLogMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.jobExecLogInfo", TableConstant.JOB_EXEC_LOG_INFO)), mongoTemplate);
    }

    @Bean("nodeLogDao")
    public NodeLogDao nodeLogDao(MongoTemplate mongoTemplate, Environment environment) {
        return new NodeLogMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.eventInfo", TableConstant.NODE_LOG_INFO)), mongoTemplate);
    }

    @Bean("groupDao")
    public GroupDao groupDao(MongoTemplate mongoTemplate, Environment environment) {
        return new GroupMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.groupInfo", TableConstant.GROUP_INFO)), mongoTemplate);
    }

    @Bean("userDao")
    public UserDao userDao(MongoTemplate mongoTemplate, Environment environment) {
        return new UserMongoDBDaoImpl(genCollectionName(collectionNamePrefix, environment.getProperty("chronus.plugin.storage.mongodb.collectionName.userInfo", TableConstant.USER_INFO)), mongoTemplate);
    }

}
