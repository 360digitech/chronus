package com.qihoo.finance.chronus.protocol.http;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.system.pojo.HttpProtocolConfig;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.protocol.api.ChronusSdkFacadeFactory;
import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import org.apache.commons.collections4.map.HashedMap;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public class ChronusSdkFacadeFactoryImpl implements ChronusSdkFacadeFactory {
    private Map<String, ChronusSdkFacade> sdkProcessorMap = new HashedMap<>();
    private static Interner<String> pool = Interners.newWeakInterner();

    @Resource
    private Environment environment;

    @Override
    public ChronusSdkFacade getInstance(SystemGroupEntity systemGroupEntity) {
        HttpProtocolConfig httpProtocolConfig = getHttpProtocolConfig(systemGroupEntity.getProtocolConfig());
        String serviceUrl = httpProtocolConfig.getServiceUrl();
        Assert.notNull(serviceUrl, systemGroupEntity.getSysCode() + " serviceUrl Cannot be empty");
        String id = genId(systemGroupEntity);
        if (sdkProcessorMap.containsKey(id)) {
            return sdkProcessorMap.get(id);
        }
        synchronized (pool.intern(id)) {
            ChronusSdkFacade chronusSdkFacade = registerBean(httpProtocolConfig);
            sdkProcessorMap.put(id, chronusSdkFacade);
        }
        return sdkProcessorMap.get(id);
    }

    private String genId(SystemGroupEntity systemGroupEntity) {
        return systemGroupEntity.getSysCode() + DateUtils.toDateTimeText(systemGroupEntity.getDateUpdated());
    }

    private ChronusSdkFacade registerBean(HttpProtocolConfig httpProtocolConfig) {
        HttpHeaders header = new HttpHeaders();
        header.setContentType(MediaType.APPLICATION_JSON);
        if (httpProtocolConfig.getHeader() != null && !httpProtocolConfig.getHeader().isEmpty()) {
            httpProtocolConfig.getHeader().entrySet().forEach(e -> header.add(e.getKey(), e.getValue()));
        }

        RestTemplateBuilder restTemplateBuilder = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofMillis(httpProtocolConfig.getTimeout()))
                .setReadTimeout(Duration.ofMillis(httpProtocolConfig.getTimeout()))
                .rootUri(httpProtocolConfig.getServiceUrl())
                .interceptors(new HeaderRequestInterceptor(header));

        RestTemplate restTemplate = restTemplateBuilder.build();
        ChronusSdkFacade chronusSdkFacade = new ChronusSdkRestTemplateFacade(restTemplate);
        return chronusSdkFacade;
    }

    private HttpProtocolConfig getHttpProtocolConfig(String protocolConfig) {
        JSONObject obj = JSONObject.parseObject(StringUtils.defaultString(protocolConfig, "{}"));
        HttpProtocolConfig httpProtocolConfig = obj.toJavaObject(HttpProtocolConfig.class);
        if (StringUtils.isBlank(httpProtocolConfig.getServiceUrl()) || Objects.equals(ChronusConstants.GLOBAL_SERVICE_URL, httpProtocolConfig.getServiceUrl())) {
            String serviceUrl = environment.getProperty("worker.global.http.address");
            Assert.notNull(serviceUrl, "globalServiceUrl serviceUrl Cannot be empty ,config ['worker.global.http.address']");
            httpProtocolConfig.setServiceUrl(serviceUrl);
        }
        return httpProtocolConfig;
    }
}
