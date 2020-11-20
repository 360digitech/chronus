package com.qihoo.finance.chronus.protocol.dubbo;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.core.system.pojo.DubboProtocolConfig;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.protocol.api.ChronusSdkFacadeFactory;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ApplicationConfig;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.RegistryConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.util.Assert;

import java.util.Map;
import java.util.Objects;

public class ChronusSdkFacadeFactoryImpl implements ChronusSdkFacadeFactory {
    private Map<String, ChronusSdkFacade> sdkProcessorMap = new HashedMap<>();
    private static Interner<String> pool = Interners.newWeakInterner();

    @Autowired
    private ApplicationConfig applicationConfig;

    @Override
    public ChronusSdkFacade getInstance(SystemGroupEntity systemGroupEntity) {
        DubboProtocolConfig dubboProtocolConfig = getDubboProtocolConfig(systemGroupEntity.getProtocolConfig());
        String registryAddress = dubboProtocolConfig.getRegistryAddress();
        Assert.notNull(registryAddress, systemGroupEntity.getSysCode() + " RegistryAddress Cannot be empty");
        String id = genBeanId(dubboProtocolConfig);
        if (sdkProcessorMap.containsKey(id)) {
            return sdkProcessorMap.get(id);
        }

        synchronized (pool.intern(id)) {
            if (sdkProcessorMap.containsKey(id)) {
                return sdkProcessorMap.get(id);
            }
            ChronusSdkFacade chronusSdkFacade = registerBean(id, dubboProtocolConfig);
            sdkProcessorMap.put(id, chronusSdkFacade);
        }
        return sdkProcessorMap.get(id);
    }

    private String genBeanId(DubboProtocolConfig dubboProtocolConfig) {
        return dubboProtocolConfig.getRegistryAddress() + dubboProtocolConfig.getTimeout();
    }

    private ChronusSdkFacade registerBean(String id, DubboProtocolConfig dubboProtocolConfig) {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setAddress(dubboProtocolConfig.getRegistryAddress());
        registryConfig.setId(dubboProtocolConfig.getRegistryAddress() + "Registry");

        ReferenceConfig<ChronusSdkFacade> reference = new ReferenceConfig<>();
        reference.setApplication(applicationConfig);
        reference.setRegistry(registryConfig);
        reference.setInterface(ChronusSdkFacade.class);
        reference.setTimeout(dubboProtocolConfig.getTimeout());
        reference.setCheck(false);
        reference.setCluster("failfast");
        reference.setLoadbalance("ChronusLoadBalance");
        reference.setId(id);
        //reference.setGroup("*");
        ChronusSdkFacade chronusSdkFacade = reference.get();
        return chronusSdkFacade;
    }


    private DubboProtocolConfig getDubboProtocolConfig(String protocolConfig) {
        JSONObject obj = JSONObject.parseObject(StringUtils.defaultString(protocolConfig, "{}"));
        DubboProtocolConfig dubboProtocolConfig = obj.toJavaObject(DubboProtocolConfig.class);
        if (StringUtils.isBlank(dubboProtocolConfig.getRegistryAddress()) || Objects.equals(ChronusConstants.GLOBAL_REGISTRY_ADDRESS, dubboProtocolConfig.getRegistryAddress())) {
            Environment environment = SpringContextHolder.getBean(Environment.class);
            String registryAddress = environment.getProperty("dubbo.registry.address");
            Assert.notNull(registryAddress, "globalRegistryAddress registryAddress Cannot be empty ,config ['dubbo.registry.address']");
            dubboProtocolConfig.setRegistryAddress(registryAddress);
        }
        return dubboProtocolConfig;
    }
}
