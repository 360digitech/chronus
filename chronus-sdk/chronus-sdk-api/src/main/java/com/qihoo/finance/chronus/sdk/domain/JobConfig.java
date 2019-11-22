package com.qihoo.finance.chronus.sdk.domain;

import java.io.Serializable;

/**
 * Created by xiongpu on 2018/6/29.
 */
public class JobConfig implements Serializable {
    private String sysCode;

    private String taskName;

    private String beanName;

    private String taskParameter;

    private boolean isBroadcastInvoker;

    public JobConfig() {

    }

    public JobConfig(String sysCode, String taskName, String beanName, String taskParameter, String isBroadcastInvoker) {
        this.sysCode = sysCode;
        this.taskName = taskName;
        this.beanName = beanName;
        this.taskParameter = taskParameter;
        this.isBroadcastInvoker = "Y".equalsIgnoreCase(isBroadcastInvoker);
    }

    public String getSysCode() {
        return sysCode;
    }

    public void setSysCode(String sysCode) {
        this.sysCode = sysCode;
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

    public boolean isBroadcastInvoker() {
        return isBroadcastInvoker;
    }

    public void setBroadcastInvoker(boolean broadcastInvoker) {
        isBroadcastInvoker = broadcastInvoker;
    }

    @Override
    public String toString() {
        return "JobConfig{" +
                "sysCode='" + sysCode + '\'' +
                ", taskName='" + taskName + '\'' +
                ", beanName='" + beanName + '\'' +
                ", taskParameter='" + taskParameter + '\'' +
                ", isBroadcastInvoker=" + isBroadcastInvoker +
                '}';
    }
}
