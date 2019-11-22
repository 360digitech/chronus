package com.qihoo.finance.chronus.metadata.api.log.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface JobExecLogDao {
    void batchInsert(List<JobExecLogEntity> jobExecLogEntityList);

    List<JobExecLogEntity> selectListAll();

    List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode);

    PageResult<JobExecLogEntity> findAllByPage(JobExecLogEntity jobExecLogEntity);


}
