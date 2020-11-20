package com.qihoo.finance.chronus.protocol.http.config;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.protocol.api.ChronusSdkFacadeFactory;
import com.qihoo.finance.chronus.protocol.http.ChronusSdkFacadeFactoryImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@ConditionalOnProperty(prefix = "http", name = {"enabled"}, matchIfMissing = true)
public class HttpConfiguration {

    @Bean(name = ChronusConstants.CHRONUS_SDK_FACADE_FACTORY + "#HTTP")
    public ChronusSdkFacadeFactory chronusSdkFacadeFactory() {
        ChronusSdkFacadeFactory chronusSdkFacadeFactory = new ChronusSdkFacadeFactoryImpl();
        return chronusSdkFacadeFactory;
    }
}
