package com.qihoo.finance.chronus.executor.service;

import com.google.common.collect.Table;
import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import com.qihoo.finance.chronus.metadata.api.assign.entity.TaskAssignResultEntity;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by xiongpu on 2019/9/9.
 */
public interface ExecutorService {

    /**
     * 移除并且暂停任务
     * 移除加载异常列表中的这个任务
     * @param taskAssignResultList 需要移除的任务列表
     * @param loadCreateErrorTable 当前加载异常的任务列表
     */
    void removeStopTask(List<TaskAssignResultEntity> taskAssignResultList,Table<String,Integer,TaskAssignResultEntity> loadCreateErrorTable);

    /**
     * 添加一个任务管理器
     * 添加异常会放入加载异常表格内
     * @param taskAssignResultList
     * @param loadCreateErrorTable
     * @return 加载结果
     */
    List<TaskAssignResultEntity> addNewTask(List<TaskAssignResultEntity> taskAssignResultList,Table<String,Integer,TaskAssignResultEntity> loadCreateErrorTable);

    /**
     * 调用master 获取分配结果
     * @param masterNodeAddress
     * @param executorTaskStateBO
     * @return
     * @throws UnsupportedEncodingException
     */
    ExecutorTaskStateBO getNodeData(String masterNodeAddress, ExecutorTaskStateBO executorTaskStateBO) throws UnsupportedEncodingException;

    /**
     * 提交加载状态到master
     * @param masterNodeAddress
     * @param executorTaskStateBO
     * @throws UnsupportedEncodingException
     */
    void submitNodeDataState(String masterNodeAddress, ExecutorTaskStateBO executorTaskStateBO) throws UnsupportedEncodingException;
}
