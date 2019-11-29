package com.qihoo.finance.chronus.metadata.api.task.dao;

import com.qihoo.finance.chronus.metadata.api.task.entity.TaskRuntimeEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface TaskRuntimeDao {
    void insert(TaskRuntimeEntity taskRuntimeEntity);

    TaskRuntimeEntity taskRuntimeIsExist(TaskRuntimeEntity taskRuntimeEntity);

    void updateTaskRuntimeHeartBeatTime(TaskRuntimeEntity taskRuntimeEntity);

    /**
     * 只会查询出心跳时间在五个心跳死亡周期之内的数据
     * 对于一个心跳死亡周期之外的数据会被标识为dead
     * @param cluster
     * @param taskName
     * @param judgeDeadInterval
     * @return
     */
    List<TaskRuntimeEntity> selectTaskRuntimeByTaskName(String cluster, String taskName,Integer judgeDeadInterval);

    void delete(TaskRuntimeEntity taskRuntimeEntity);
}
