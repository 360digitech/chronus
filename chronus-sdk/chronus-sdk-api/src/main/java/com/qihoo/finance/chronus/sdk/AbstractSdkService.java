package com.qihoo.finance.chronus.sdk;

import com.qihoo.finance.chronus.sdk.annotation.Job;
import com.qihoo.finance.chronus.sdk.annotation.JobMethod;
import com.qihoo.finance.chronus.sdk.domain.JobData;
import com.qihoo.finance.chronus.sdk.enums.ChronusSdkErrorCodeEnum;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkExecuteFlowService;
import com.qihoo.finance.chronus.sdk.service.ChronusSdkSimpleJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by xiongpu on 2019/8/29.
 */
@Slf4j
public abstract class AbstractSdkService<T> implements ChronusSdkFacade<T>, ApplicationContextAware {
    private static final Map<String, Method> jobAnnotationMap = new HashMap<>();
    private static ApplicationContext applicationContext = null;

    @Autowired(required = false)
    protected Map<String, ChronusSdkExecuteFlowService> dataFlowServiceMap;

    @Autowired(required = false)
    protected Map<String, ChronusSdkSimpleJob> simpleServiceMap;

    @Override
    public List<T> selectTasks(JobData jobData) throws Exception {
        ChronusSdkExecuteFlowService clientService = dataFlowServiceMap.get(jobData.getBeanName());
        checkJobConfig(jobData, clientService);
        try {
            List<T> data = clientService.selectTasks(jobData);
            return data;
        } catch (Throwable e) {
            log.error("jobData:{} selectTasks error", jobData, e);
            throw e;
        }
    }

    @Override
    public boolean execute(JobData jobData, List<T> itemList) throws Exception{
        try {
            ChronusSdkExecuteFlowService clientService = dataFlowServiceMap.get(jobData.getBeanName());
            return clientService.execute(jobData, itemList);
        } catch (Throwable e) {
            log.error("jobData:{} ,itemList:{} execute error", jobData, itemList, e);
        }
        return false;
    }


    @Override
    public boolean execute(JobData jobData) {
        ChronusSdkSimpleJob sdkSimpleService = simpleServiceMap.get(jobData.getBeanName());
        try {
            if (sdkSimpleService != null) {
                return sdkSimpleService.execute(jobData);
            } else {
                Method method = jobAnnotationMap.get(jobData.getBeanName());
                return invoke(jobData.getBeanName(), method, jobData);
            }
        } catch (Throwable e) {
            log.error("jobData:{} ,execute error", jobData, e);
        }
        return false;
    }


    private void checkJobConfig(JobData jobData, ChronusSdkExecuteFlowService clientService) {
        if (clientService == null) {
            throw new RuntimeException(" [" + jobData.getServiceName() + " : " + jobData.getBeanName() + "] [" + ChronusSdkErrorCodeEnum.HANDLE_BEAN_NOT_EXIST.getCode() + "]" + ChronusSdkErrorCodeEnum.HANDLE_BEAN_NOT_EXIST.getMsg());
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        AbstractSdkService.applicationContext = applicationContext;
        try {
            Map<String, Object> jobBeans = applicationContext.getBeansWithAnnotation(Job.class);
            Set<Method> methodSet = new HashSet<>();
            for (Map.Entry<String, Object> entry : jobBeans.entrySet()) {
                Object bean = entry.getValue();
                Method[] methods = bean.getClass().getMethods();
                for (Method method : methods) {
                    if (method.isAnnotationPresent(JobMethod.class)) {
                        methodSet.add(method);
                    }
                }
            }
            if (methodSet.size() == 0) {
                return;
            }
            for (Method method : methodSet) {
                JobMethod annotation = method.getAnnotation(JobMethod.class);
                if (annotation == null) {
                    continue;
                }
                if ("".equals(annotation.key().trim()) || !annotation.key().contains(".") || annotation.key().split("\\.").length != 2) {
                    throw new IllegalArgumentException("method:" + method.getName() + " @JobMethod.key error:" + annotation.key() + " need 'beanId.methodName'");
                }
                String key = annotation.key();
                String beanName = key.split("\\.")[0];
                // bean 是否存在
                applicationContext.getBean(beanName);
                if (jobAnnotationMap.containsKey(key)) {
                    throw new IllegalArgumentException("method:" + method.getName() + " @JobMethod.key error:" + annotation.key() + " Duplicate key");
                }
                jobAnnotationMap.put(key, method);
            }
        } catch (Throwable var13) {
            throw var13;
        }
    }

    private boolean invoke(String key, Method method, JobData jobData) throws InvocationTargetException, IllegalAccessException {
        String beanName = key.split("\\.")[0];
        Object bean = applicationContext.getBean(beanName);
        Object[] args = new Object[1];
        args[0] = jobData;
        return (boolean) method.invoke(bean, args);
    }
}
