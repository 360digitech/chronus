package com.qihoo.finance.chronus.master.service;

import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

public interface TaskAssignRefreshService {

    void restartRefreshTask();

    void shutdownRefreshTask();

    DeferredResult<ResponseEntity<WorkerTaskStateBO>> pullAssignResult(String address, String dataVersion);

    void submitWorkerState(String address, String dataVersion, WorkerTaskStateBO workerTaskStateBO);
}
