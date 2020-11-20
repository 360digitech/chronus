package com.qihoo.finance.chronus.worker.service;

import com.qihoo.finance.chronus.sdk.ChronusSdkFacade;
import com.qihoo.finance.chronus.sdk.domain.JobData;

import java.util.List;

public interface JobDispatcher {
    void init(ChronusSdkFacade chronusSdkFacade, JobData jobData);

    JobData getJobData();

    List selectTasks();

    boolean execute(List itemList);

    boolean execute();
}
