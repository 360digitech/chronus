package com.qihoo.finance.chronus.config;

import com.qihoo.finance.chronus.Bootstrap;
import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.util.NetUtils;
import com.qihoo.finance.chronus.core.event.service.EventService;
import com.qihoo.finance.chronus.core.event.service.impl.EventServiceImpl;
import com.qihoo.finance.chronus.support.BootstrapSupport;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongpu on 2019/8/28.
 */
@Configuration
@EnableConfigurationProperties({BootstrapProperties.class})
public class BootstrapConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        //先获取到converter列表
        List<HttpMessageConverter<?>> converters = builder.build().getMessageConverters();
        for (HttpMessageConverter<?> converter : converters) {
            //因为我们只想要jsonConverter支持对text/html的解析
            if (converter instanceof MappingJackson2HttpMessageConverter) {
                //先将原先支持的MediaType列表拷出
                List<MediaType> mediaTypeList = new ArrayList<>(converter.getSupportedMediaTypes());
                //加入对text/html的支持
                mediaTypeList.add(MediaType.TEXT_HTML);
                //将已经加入了text/html的MediaType支持列表设置为其支持的媒体类型列表
                ((MappingJackson2HttpMessageConverter) converter).setSupportedMediaTypes(mediaTypeList);
            }
        }
        return builder.build();
    }


    @Bean
    @ConditionalOnMissingBean
    public Bootstrap bootstrap() {
        Bootstrap bootstrap = new Bootstrap();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                bootstrap.stop();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }));
        return bootstrap;
    }

    @Order(100)
    @Bean(name = "support#Bootstrap")
    @ConditionalOnExpression("${chronus.executor.enabled:true} || ${chronus.master.enabled:true}")
    public BootstrapSupport bootstrapSupport() {
        BootstrapSupport bootstrap = new BootstrapSupport();
        return bootstrap;
    }

    @Bean(name = "eventService")
    public EventService eventService() {
        EventService eventService = new EventServiceImpl();
        return eventService;
    }


    @Bean(name = "currentNode")
    public NodeInfo currentNode(Environment environment, BootstrapProperties bootstrapProperties) throws UnknownHostException {
        String cluster = environment.getProperty(bootstrapProperties.getClusterKey(), "default");
        NodeInfo currentNode = new NodeInfo();
        currentNode.setCluster(cluster);
        currentNode.setIp(NetUtils.getLocalIP());
        currentNode.setPort(Integer.valueOf(environment.getProperty("server.port", "8080")));
        currentNode.setAddress(currentNode.getIp() + ":" + currentNode.getPort());
        currentNode.setHostName(NetUtils.getLocalHost());

        currentNode.setEnableMaster(Boolean.valueOf(environment.getProperty("chronus.master.enabled", Boolean.FALSE.toString())) ? ChronusConstants.Y : ChronusConstants.N);
        currentNode.setEnableExecutor(Boolean.valueOf(environment.getProperty("chronus.executor.enabled", Boolean.FALSE.toString())) ? ChronusConstants.Y : ChronusConstants.N);
        currentNode.setVersion(String.valueOf(System.currentTimeMillis()));
        return currentNode;
    }

}
