package com.qihoo.finance.chronus.core.log.service;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/4/13.
 */
public interface JobExecLogService {

    void batchInsert(List<JobExecLogEntity> jobExecLogEntityList);

    List<JobExecLogEntity> selectListAll();

    List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode);

    PageResult<JobExecLogEntity> selectListByPage(JobExecLogEntity jobExecLogEntity);
}
