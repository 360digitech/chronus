package com.qihoo.finance.chronus.sdk.domain;

import java.io.Serializable;

/**
 * Created by xiongpu on 2018/6/29.
 */
public class TaskItemDefineDomain implements Serializable {
    private String taskItemId;
    private String parameter;

    public TaskItemDefineDomain() {
    }

    public void setParameter(String parameter) {
        this.parameter = parameter;
    }

    public String getParameter() {
        return this.parameter;
    }

    public void setTaskItemId(String taskItemId) {
        this.taskItemId = taskItemId;
    }

    public String getTaskItemId() {
        return this.taskItemId;
    }

    @Override
    public String toString() {
        return "TaskItemDefineDomain{" +
                "taskItemId='" + taskItemId + '\'' +
                ", parameter='" + parameter + '\'' +
                '}';
    }
}
