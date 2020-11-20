package com.qihoo.finance.chronus.config;

import com.qihoo.finance.chronus.Bootstrap;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.util.NetUtils;
import com.qihoo.finance.chronus.core.log.NodeLogService;
import com.qihoo.finance.chronus.core.log.impl.NodeLogServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@EnableConfigurationProperties({BootstrapProperties.class})
public class BootstrapConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate;
    }

    @Bean
    @ConditionalOnMissingBean
    public Bootstrap bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        return bootstrap;
    }

    @Bean(name = "nodeLogService")
    public NodeLogService nodeLogService() {
        NodeLogService nodeLogService = new NodeLogServiceImpl();
        return nodeLogService;
    }


    @Bean(name = "currentNode")
    public NodeInfo currentNode(Environment environment, BootstrapProperties bootstrapProperties) throws UnknownHostException {
        String cluster = environment.getProperty(bootstrapProperties.getClusterKey(), "default");
        String tag = environment.getProperty(bootstrapProperties.getTagKey(), ChronusConstants.DEF_TAG);

        NodeInfo currentNode = new NodeInfo();
        currentNode.setCluster(cluster);
        currentNode.setTag(tag);
        currentNode.setIp(NetUtils.getLocalIP());
        currentNode.setPort(Integer.valueOf(environment.getProperty("server.port", "8080")));
        currentNode.setAddress(currentNode.getIp() + ":" + currentNode.getPort());
        currentNode.setHostName(NetUtils.getLocalHost());

        currentNode.setEnableMaster(Boolean.valueOf(environment.getProperty("chronus.master.enabled", Boolean.FALSE.toString())) ? ChronusConstants.Y : ChronusConstants.N);
        currentNode.setEnableWorker(Boolean.valueOf(environment.getProperty("chronus.worker.enabled", Boolean.FALSE.toString())) ? ChronusConstants.Y : ChronusConstants.N);
        currentNode.setVersion(String.valueOf(System.currentTimeMillis()));
        return currentNode;
    }
}
