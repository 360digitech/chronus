package com.qihoo.finance.chronus.executor.bo;

import com.qihoo.finance.chronus.dispatcher.TaskDealService;
import com.qihoo.finance.chronus.executor.service.TaskManager;
import com.qihoo.finance.chronus.metadata.api.task.bo.TaskItemDefine;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Created by xiongpu on 2019/8/6.
 */
@Getter
@Builder
public class ProcessorParam {
    /**
     * 任务管理器
     */
    private TaskManager taskManager;
    /**
     * 任务信息
     */
    private TaskEntity taskEntity;
    /**
     * 任务处理bean
     */
    private TaskDealService taskDealBean;

    /**
     * 任务项
     */
    private List<TaskItemDefine> taskItems;
    /**
     * 任务运行信息
     */
    private volatile TaskRuntimeEntity taskRuntime;
}
