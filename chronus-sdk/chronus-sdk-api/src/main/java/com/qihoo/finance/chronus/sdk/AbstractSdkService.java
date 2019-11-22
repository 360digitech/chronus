package com.qihoo.finance.chronus.sdk;

import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import com.qihoo.finance.chronus.sdk.enums.ChronusSdkErrorCodeEnum;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkBatchJob;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSelectTaskService;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSingleJob;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by xiongpu on 2019/8/29.
 */
@Slf4j
public abstract class AbstractSdkService<T> implements ChronusSdkProcessor<T> {

    private String version;

    @Autowired(required = false)
    protected Map<String, ChronusSdkSelectTaskService> dataFlowServiceMap;

    @Autowired(required = false)
    protected Map<String, ChronusSdkSimpleJob> simpleServiceMap;

    @Override
    public String getVersion() {
        if (version != null) {
            return version;
        }
        try {
            Properties properties = new Properties();
            properties.load(this.getClass().getResourceAsStream("/META-INF/maven/com.qihoo.finance.chronus/chronus-sdk-common/pom.properties"));
            this.version = properties.getProperty("version");
        } catch (Exception e) {
            log.error("获取客户端版本号异常!", e);
        }
        return this.version;
    }

    @Override
    public boolean execute(JobConfig jobConfig, T item) {
        try {
            ChronusSdkSingleJob clientService = (ChronusSdkSingleJob) dataFlowServiceMap.get(jobConfig.getBeanName());
            return clientService.execute(item, jobConfig.getTaskParameter());
        } catch (Throwable e) {
            log.error("Bean:{} ,taskParameter:{} ,item:{} execute error", jobConfig.getBeanName(), jobConfig.getTaskParameter(), item, e);
        }
        return false;
    }

    @Override
    public boolean execute(JobConfig jobConfig, List taskItemList, int eachFetchDataNum) {
        ChronusSdkSimpleJob sdkSimpleService = simpleServiceMap.get(jobConfig.getBeanName());
        try {
            return sdkSimpleService.execute(jobConfig.getTaskParameter(), taskItemList, eachFetchDataNum);
        } catch (Throwable e) {
            log.error("Bean:{} ,taskParameter:{} ,taskItemList:{} execute error", jobConfig.getBeanName(), jobConfig.getTaskParameter(), taskItemList, e);
        }
        return false;
    }

    @Override
    public boolean executeBatch(JobConfig jobConfig, T[] itemArr) {
        try {
            ChronusSdkBatchJob clientService = (ChronusSdkBatchJob) dataFlowServiceMap.get(jobConfig.getBeanName());
            return clientService.execute(itemArr, jobConfig.getTaskParameter());
        } catch (Throwable e) {
            log.error("Bean:{} ,taskParameter:{} ,item:{} execute error", jobConfig.getBeanName(), jobConfig.getTaskParameter(), itemArr, e);
        }
        return false;
    }

    @Override
    public List<T> selectTasks(JobConfig jobConfig, List taskItemList, int eachFetchDataNum) throws Exception {
        ChronusSdkSelectTaskService clientService = dataFlowServiceMap.get(jobConfig.getBeanName());
        checkJobConfig(jobConfig, clientService);
        try {
            List<T> data = clientService.selectTasks(jobConfig.getTaskParameter(), taskItemList, eachFetchDataNum);
            return data;
        } catch (Throwable e) {
            log.error("Bean:{} ,taskParameter:{} selectTasks error", jobConfig.getBeanName(), jobConfig.getTaskParameter(), e);
            throw e;
        }
    }

    private void checkJobConfig(JobConfig jobConfig, ChronusSdkSelectTaskService clientService) {
        if (clientService == null) {
            throw new RuntimeException(" [" + jobConfig.getSysCode() + " : " + jobConfig.getBeanName() + "] [" + ChronusSdkErrorCodeEnum.HANDLE_BEAN_NOT_EXIST.getCode() + "]" + ChronusSdkErrorCodeEnum.HANDLE_BEAN_NOT_EXIST.getMsg());
        }
    }
}
