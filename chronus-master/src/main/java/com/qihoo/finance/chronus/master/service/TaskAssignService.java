package com.qihoo.finance.chronus.master.service;

import com.qihoo.finance.chronus.metadata.api.assign.bo.ExecutorTaskStateBO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.context.request.async.DeferredResult;

/**
 * Created by xiongpu on 2019/9/16.
 */
public interface TaskAssignService {

    void clear();

    DeferredResult<ResponseEntity<ExecutorTaskStateBO>> pullAssignResult(String address, ExecutorTaskStateBO executorTaskStateBO);

    void submitExecutorState(String address, ExecutorTaskStateBO executorTaskStateBO) throws InterruptedException;

    void taskAssign() throws InterruptedException;
}
