package com.qihoo.finance.chronus.worker.service;

import com.qihoo.finance.chronus.metadata.api.assign.bo.WorkerTaskStateBO;
import com.qihoo.finance.chronus.registry.api.Node;

public interface WorkerReceiveProcess {

    WorkerReceiveProcess init(Node node, WorkerTaskStateBO workerTaskStateBO,String masterNodeAddress);

    WorkerReceiveProcess submitState();

    WorkerReceiveProcess getAssignResult();

    WorkerReceiveProcess receiveProcess();

    WorkerReceiveProcess finish();

    WorkerTaskStateBO getWorkerTaskStateBO();
}
