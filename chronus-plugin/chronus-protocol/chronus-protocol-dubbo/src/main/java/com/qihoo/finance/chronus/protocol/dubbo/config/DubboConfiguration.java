package com.qihoo.finance.chronus.protocol.dubbo.config;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.protocol.api.ChronusSdkFacadeFactory;
import com.qihoo.finance.chronus.protocol.dubbo.ChronusSdkFacadeFactoryImpl;
import org.apache.dubbo.spring.boot.autoconfigure.DubboAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@AutoConfigureAfter(DubboAutoConfiguration.class)
@ConditionalOnProperty(prefix = "dubbo", name = {"enabled"}, matchIfMissing = true)
public class DubboConfiguration {

    @Bean(name = ChronusConstants.CHRONUS_SDK_FACADE_FACTORY + "#DUBBO")
    public ChronusSdkFacadeFactory chronusSdkFacadeFactory() {
        ChronusSdkFacadeFactory chronusSdkFacadeFactory = new ChronusSdkFacadeFactoryImpl();
        return chronusSdkFacadeFactory;
    }
}
