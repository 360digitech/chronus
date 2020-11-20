package com.qihoo.finance.chronus.core.task;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskItemEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.List;
import java.util.Set;

/**
 * Created by xiongpu on 2019/8/15.
 */
public interface TaskItemService {
    /**
     * 获取任务项信息
     * @param taskItemId
     * @return
     */
    TaskItemEntity getTaskItem(String taskItemId);

    List<TaskItemEntity> getTaskItemList(String taskName);

    void create(TaskItemEntity taskItemEntity);

    void delete(TaskItemEntity taskItemEntity);

    void deleteByTaskItemInfo(TaskItemEntity taskItemEntity);

    List<TaskItemEntity> selectAllTaskItem();
    List<TaskItemEntity> selectTaskItemByCluster(String cluster);

    /**
     * 更新worker节点任务加载结果
     * @param taskItemEntity
     */
    void update(TaskItemEntity taskItemEntity);

    /**
     * 当任务从不确定状态 需要重新启动时,先去拿锁
     * @param taskItemEntity
     * @return
     */
    boolean restartLock(TaskItemEntity taskItemEntity);
}
