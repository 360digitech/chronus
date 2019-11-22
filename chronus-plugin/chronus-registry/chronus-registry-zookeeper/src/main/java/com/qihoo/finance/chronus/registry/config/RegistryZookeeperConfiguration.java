package com.qihoo.finance.chronus.registry.config;

import com.qihoo.finance.chronus.registry.api.MasterElectionService;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.registry.util.ZookeeperManager;
import com.qihoo.finance.chronus.registry.zookeeper.MasterElectionServiceZookeeperImpl;
import com.qihoo.finance.chronus.registry.zookeeper.NamingServiceZookeeperImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Properties;

/**
 * Created by xiongpu on 2019/9/21.
 */
@Configuration
@EnableConfigurationProperties({RegistryZookeeperProperties.class})
@ConditionalOnProperty(prefix = "chronus", name = {"registry"}, havingValue = "zookeeper", matchIfMissing = true)
public class RegistryZookeeperConfiguration {

    /**
     * 基于Zk的Master策略选举类
     *
     * @return
     */
    @Bean("masterElectionStrategy#zookeeper")
    public MasterElectionService masterElectionStrategyDefaultService() {
        MasterElectionService masterElectionStrategyService = new MasterElectionServiceZookeeperImpl();
        return masterElectionStrategyService;
    }

    @Bean
    public NamingService namingService() {
        NamingService namingService = new NamingServiceZookeeperImpl();
        return namingService;
    }

    @Bean
    public ZookeeperManager zkManager(RegistryZookeeperProperties registryZookeeperProperties, Environment environment) throws Exception {
        String zkAddress = registryZookeeperProperties.getAddress();
        if (StringUtils.isBlank(zkAddress)) {
            zkAddress = environment.getProperty("dubbo.registry.address");
            if (StringUtils.isNotBlank(zkAddress)) {
                //zookeeper://127.0.0.1:2181?backup=127.0.0.1:2182,127.0.0.1:2183
                zkAddress = zkAddress.replace("zookeeper://", "");
                zkAddress = zkAddress.replace("?backup=", ",");
            }
        }
        if(StringUtils.isBlank(zkAddress)){
            zkAddress="127.0.0.1:2181";
        }

        Properties properties = new Properties();
        properties.setProperty(ZookeeperManager.keys.zkConnectString.toString(),zkAddress);
        properties.setProperty(ZookeeperManager.keys.rootPath.toString(),registryZookeeperProperties.getRootPath());
        properties.setProperty(ZookeeperManager.keys.userName.toString(),registryZookeeperProperties.getUserName());
        properties.setProperty(ZookeeperManager.keys.password.toString(),registryZookeeperProperties.getPassword());
        properties.setProperty(ZookeeperManager.keys.zkSessionTimeout.toString(),String.valueOf(registryZookeeperProperties.getSessionTimeout()));
        properties.setProperty(ZookeeperManager.keys.zkConnectionTimeout.toString(),String.valueOf(registryZookeeperProperties.getConnectionTimeout()));
        properties.setProperty(ZookeeperManager.keys.isCheckParentPath.toString(),String.valueOf(registryZookeeperProperties.isCheckParentPath()));

        ZookeeperManager zkManager = new ZookeeperManager(properties);
        return zkManager;
    }
}
