package com.qihoo.finance.chronus.dispatcher;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;

/**
 * 任务处理bean的基础Service
 * Created by xiongpu on 2019/9/14.
 */
public interface TaskDealService {

    void setTaskInfo(TaskEntity taskTypeInfo);
}
