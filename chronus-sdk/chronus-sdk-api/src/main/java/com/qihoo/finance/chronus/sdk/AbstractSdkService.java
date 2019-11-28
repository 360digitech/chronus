package com.qihoo.finance.chronus.sdk;

import com.qihoo.finance.chronus.sdk.annotation.Job;
import com.qihoo.finance.chronus.sdk.domain.JobConfig;
import com.qihoo.finance.chronus.sdk.domain.TaskItemDefineDomain;
import com.qihoo.finance.chronus.sdk.enums.ChronusSdkErrorCodeEnum;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkBatchJob;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSelectTaskService;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSimpleJob;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSingleJob;
import lombok.extern.slf4j.Slf4j;
import org.reflections.Reflections;
import org.reflections.scanners.MethodAnnotationsScanner;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import javax.annotation.PostConstruct;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by xiongpu on 2019/8/29.
 */
@Slf4j
public abstract class AbstractSdkService<T> implements ChronusSdkProcessor<T>, ApplicationContextAware {
    private static final Map<String, Method> jobAnnotationMap = new HashMap<>();
    private static ApplicationContext applicationContext = null;

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
            properties.load(this.getClass().getResourceAsStream("/META-INF/maven/com.qihoo.finance.chronus/chronus-sdk-api/pom.properties"));
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
    public boolean execute(JobConfig jobConfig, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) {
        ChronusSdkSimpleJob sdkSimpleService = simpleServiceMap.get(jobConfig.getBeanName());
        try {
            if (sdkSimpleService != null) {
                return sdkSimpleService.execute(jobConfig.getTaskParameter(), taskItemList, eachFetchDataNum);
            } else {
                Method method = jobAnnotationMap.get(jobConfig.getBeanName());
                return invoke(jobConfig.getBeanName(), method, jobConfig.getTaskParameter(), taskItemList, eachFetchDataNum);
            }
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
    public List<T> selectTasks(JobConfig jobConfig, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws Exception {
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


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AbstractSdkService.applicationContext = applicationContext;
    }


    @PostConstruct
    public void scanner() {
        try {
            Reflections reflections = new Reflections(new MethodAnnotationsScanner());
            Set<Method> resources = reflections.getMethodsAnnotatedWith(Job.class);
            if (resources == null || resources.size() < 1) {
                return;
            }
            for (Method method : resources) {
                Job annotation = method.getAnnotation(Job.class);
                if (annotation == null) {
                    continue;
                }
                if ("".equals(annotation.key().trim()) || !annotation.key().contains(".") || annotation.key().split("\\.").length != 2) {
                    throw new IllegalArgumentException("method:" + method.getName() + " @Job.key error:" + annotation.key() + " need 'beanId.methodName'");
                }
                String key = annotation.key();
                String beanName = key.split("\\.")[0];
                // bean 是否存在
                applicationContext.getBean(beanName);
                if(jobAnnotationMap.containsKey(key)){
                    throw new IllegalArgumentException("method:" + method.getName() + " @Job.key error:" + annotation.key() + " Duplicate key");
                }
                jobAnnotationMap.put(key, method);
            }
        } catch (Throwable var13) {
            throw var13;
        }
    }


    private boolean invoke(String key, Method method, String taskParameter, List<TaskItemDefineDomain> taskItemList, int eachFetchDataNum) throws InvocationTargetException, IllegalAccessException {
        String beanName = key.split("\\.")[0];
        Object bean = applicationContext.getBean(beanName);
        Class[] paramTypes = method.getParameterTypes();

        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            Object arg = null;
            if (paramTypes[i].isAssignableFrom(String.class)) {
                arg = taskParameter;
            } else if (paramTypes[i].isAssignableFrom(List.class)) {
                arg = taskItemList;
            } else if (paramTypes[i].isAssignableFrom(int.class) || paramTypes[i].isAssignableFrom(Integer.class)) {
                arg = eachFetchDataNum;
            }
            args[i] = arg;
        }
        return (boolean) method.invoke(bean, args);
    }
}
