package com.qihoo.finance.chronus.worker.processor;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by xiongpu on 2019/8/16.
 */
@Slf4j
public abstract class AbstractSelectExecuteProcessor<T> extends AbstractProcessor {
    protected List<T> taskList = new CopyOnWriteArrayList<>();

    /**
     * 需要注意的是，调度服务器从配置中心注销的工作，必须在所有线程退出的情况下才能做
     *
     * @throws Exception
     */
    @Override
    public void shutdown() {
        super.shutdown();
        this.taskList.clear();
    }


    public synchronized List getScheduleTaskId() {
        if (this.taskList.size() > 0) {
            int size = taskList.size() > this.processorParam.getTaskEntity().getExecuteNumber() ? this.processorParam.getTaskEntity().getExecuteNumber() : taskList.size();
            List result = null;
            if (size > 0) {
                result = new ArrayList(size);
            }
            for (int i = 0; i < size; i++) {
                // 按正序处理
                result.add(this.taskList.remove(0));
            }
            return result;
        }
        return null;
    }

    protected int loadScheduleData() {
        List<T> tmpList = processorParam.getJobDispatcher().selectTasks();
        if (tmpList != null) {
            this.taskList.addAll(tmpList);
        }
        return this.taskList.size();
    }

    protected List<List> getTaskList() {
        List<List> taskList = new ArrayList<>();
        while (true) {
            List executeTask = this.getScheduleTaskId();
            if (this.isStop() || executeTask == null) {
                break;
            }
            taskList.add(executeTask);
        }
        return taskList;
    }
}
