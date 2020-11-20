package com.qihoo.finance.chronus.sdk.domain;

import java.io.Serializable;
import java.util.List;

/**
 * Created by xiongpu on 2018/6/29.
 */
public class JobData implements Serializable {
    private String serviceName;

    /**
     * 任务名称
     */
    private String taskName;

    /**
     * 任务指定的beanName
     */
    private String beanName;

    /**
     * 配置的任务参数
     */
    private String taskParameter;

    /**
     * 分配的任务项
     */
    private List<TaskItemDefineDomain> taskItemList;

    /**
     * 每次获取数量
     */
    private int eachFetchDataNum;

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public String getTaskParameter() {
        return taskParameter;
    }

    public void setTaskParameter(String taskParameter) {
        this.taskParameter = taskParameter;
    }

    public List<TaskItemDefineDomain> getTaskItemList() {
        return taskItemList;
    }

    public void setTaskItemList(List<TaskItemDefineDomain> taskItemList) {
        this.taskItemList = taskItemList;
    }

    public int getEachFetchDataNum() {
        return eachFetchDataNum;
    }

    public void setEachFetchDataNum(int eachFetchDataNum) {
        this.eachFetchDataNum = eachFetchDataNum;
    }

    @Override
    public String toString() {
        return "JobData{" +
                "serviceName='" + serviceName + '\'' +
                ", taskName='" + taskName + '\'' +
                ", beanName='" + beanName + '\'' +
                ", taskParameter='" + taskParameter + '\'' +
                ", taskItemList='" + taskItemList + '\'' +
                ", eachFetchDataNum=" + eachFetchDataNum +
                '}';
    }
}
