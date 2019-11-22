package com.qihoo.finance.chronus.storage.h2.plugin.config;

import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.node.dao.NodeDao;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskRuntimeDao;
import com.qihoo.finance.chronus.storage.h2.plugin.dao.impl.*;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */
@Configuration
@ConditionalOnProperty(prefix = "chronus.plugin", name = "storage", havingValue = "h2")
public class H2DaoConfiguration {

    @Bean("clusterDao")
    public ClusterDao clusterDao() {
        return new ClusterH2DaoImpl();
    }

    @Bean("tagDao")
    public TagDao tagDao() {
        return new TagH2DaoImpl();
    }

    @Bean("nodeDao")
    @ConditionalOnProperty(prefix = "chronus", name = {"registry"}, havingValue = "db")
    public NodeDao nodeDao() {
        return new NodeH2DaoImpl();
    }

    @Bean("taskDao")
    public TaskDao taskDao() {
        return new TaskH2DaoImpl();
    }

    @Bean("taskRuntimeDao")
    public TaskRuntimeDao taskRuntimeDao() {
        return new TaskRuntimeH2DaoImpl();
    }

    @Bean("systemGroupDao")
    public SystemGroupDao systemGroupDao() {
        return new SystemGroupH2DaoImpl();
    }

    @Bean("jobExecLogDao")
    public JobExecLogDao jobExecLogDao() {
        return new JobExecLogH2DaoImpl();
    }
}
