package com.qihoo.finance.chronus.core.log.service.impl;

import com.qihoo.finance.chronus.core.log.service.JobExecLogService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xiongpu on 2019/4/13.
 */
@Slf4j
@Service("jobExecLogService")
public class JobExecLogServiceImpl implements JobExecLogService {

    @Resource
    private JobExecLogDao jobExecLogDao;

    @Override
    public void batchInsert(List<JobExecLogEntity> jobExecLogEntityList) {
        jobExecLogDao.batchInsert(jobExecLogEntityList);
    }

    @Override
    public List<JobExecLogEntity> selectListAll() {
        return jobExecLogDao.selectListAll();
    }

    @Override
    public List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode) {
        return jobExecLogDao.selectListByCluster(cluster, taskName, sysCode);
    }

    @Override
    public PageResult<JobExecLogEntity> selectListByPage(JobExecLogEntity jobExecLogEntity) {
        return jobExecLogDao.findAllByPage(jobExecLogEntity);
    }
}
