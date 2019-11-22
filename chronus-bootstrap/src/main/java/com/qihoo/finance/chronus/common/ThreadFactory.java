package com.qihoo.finance.chronus.common;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

/**
 * Created by xiongpu on 2019/1/4.
 */
public class ThreadFactory implements java.util.concurrent.ThreadFactory {

    private String threadName;

    public ThreadFactory(TaskEntity taskEntity) {
        this.threadName = taskEntity.getDealSysCode() + "-" + taskEntity.getTaskName() + "-exe";
    }

    public ThreadFactory(String threadName) {
        this.threadName = threadName;
    }

    public ThreadFactory(String supportName, String taskName) {
        this.threadName = String.join("-", supportName, taskName);
    }


    @Override
    public Thread newThread(Runnable r) {
        return new Thread(r, threadName);
    }
}
