package com.qihoo.finance.chronus.worker.bo;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public final class JobContext {

    private String key;

    private TaskEntity taskEntity;

    private volatile TaskRuntimeEntity taskRuntime;

}
