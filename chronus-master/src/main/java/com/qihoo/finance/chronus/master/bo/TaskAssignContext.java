package com.qihoo.finance.chronus.master.bo;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.google.common.collect.Table;
import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import lombok.Getter;

import java.util.Comparator;
import java.util.Map;

@Getter
public class TaskAssignContext {
    //执行机状态信息 row address,columnKey 数据版本,value WorkerTaskStateBO
    private volatile static Table<String, String, WorkerTaskStateBO> workerTaskStateTable = HashBasedTable.create();
    private volatile static Table<String, String, WorkerTaskStateBO> assignResultMap = HashBasedTable.create();
    private static Interner<String> pool = Interners.newWeakInterner();

    public static boolean putIfAbsentTaskState(String address, WorkerTaskStateBO workerTaskStateBO) {
        if (!workerTaskStateTable.contains(address, workerTaskStateBO.getDataVersion())) {
            workerTaskStateTable.put(address, workerTaskStateBO.getDataVersion(), workerTaskStateBO);
            return true;
        }
        return false;
    }

    public static WorkerTaskStateBO getTaskState(String address) {
        if (!workerTaskStateTable.containsRow(address)) {
            return null;
        }
        Map<String, WorkerTaskStateBO> workerTaskStateColumnMap = workerTaskStateTable.row(address);
        if (workerTaskStateColumnMap == null || workerTaskStateColumnMap.isEmpty()) {
            return null;
        }
        String maxKey = workerTaskStateColumnMap.keySet().stream().max(Comparator.comparingLong(e -> Long.valueOf(e))).get();
        return workerTaskStateTable.get(address, maxKey);
    }

    public static WorkerTaskStateBO getTaskState(String address, String dataVersion) {
        WorkerTaskStateBO workerTaskStateBO = workerTaskStateTable.get(address, dataVersion);
        if (workerTaskStateBO != null) {
            workerTaskStateBO.init();
        }
        return workerTaskStateBO;
    }

    public static WorkerTaskStateBO assignResultMapGet(String address, String dataVersion) {
        return assignResultMap.get(address, dataVersion);
    }

    public static void clear() {
        assignResultMap.clear();
        workerTaskStateTable.clear();
    }

    /**
     * 任务分配前需要清理
     * 不影响下次分配的结果处理
     */
    public static void taskAssignBeforeClear() {
        assignResultMap.clear();
    }

    public static void assignResultMapPut(String address, String dataVersion, WorkerTaskStateBO workerTaskStateBO) {
        assignResultMap.put(address, dataVersion, workerTaskStateBO);
    }
}
