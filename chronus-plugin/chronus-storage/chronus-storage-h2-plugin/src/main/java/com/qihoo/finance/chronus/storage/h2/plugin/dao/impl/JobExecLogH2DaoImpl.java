package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.JobExecLogH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.JobExecLogJpaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class JobExecLogH2DaoImpl implements JobExecLogDao {
    @Autowired
    private JobExecLogJpaRepository jobExecLogJpaRepository;

    @Override
    public void batchInsert(List<JobExecLogEntity> jobExecLogEntityList) {
        jobExecLogJpaRepository.saveAll(transferH2EntityList(jobExecLogEntityList));
    }

    @Override
    public List<JobExecLogEntity> selectListAll() {
        return transferEntityList(jobExecLogJpaRepository.findAll());

    }

    @Override
    public List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode) {
        return transferEntityList(jobExecLogJpaRepository.selectListByCluster(cluster, taskName, sysCode));
    }


    @Override
    public PageResult<JobExecLogEntity> findAllByPage(JobExecLogEntity jobExecLogEntity) {
        //TODO
        return null;
    }

    private JobExecLogH2Entity transferH2Entity(JobExecLogEntity entity) {
        JobExecLogH2Entity h2Entity = new JobExecLogH2Entity();
        BeanUtils.copyProperties(entity, h2Entity);
        h2Entity.setId(Long.valueOf(entity.getId()));
        return h2Entity;
    }

    private List<JobExecLogH2Entity> transferH2EntityList(List<JobExecLogEntity> entityList) {
        if (CollectionUtils.isEmpty(entityList)) {
            throw new IllegalArgumentException("jobExecLogEntityList must not be empty");
        }

        return entityList.stream()
                .map(this::transferH2Entity)
                .collect(Collectors.toList());
    }

    private JobExecLogEntity transferEntity(JobExecLogH2Entity h2Entity) {
        JobExecLogEntity entity = new JobExecLogEntity();
        BeanUtils.copyProperties(h2Entity, entity);
        entity.setId(String.valueOf(h2Entity.getId()));
        return entity;
    }

    private List<JobExecLogEntity> transferEntityList(List<JobExecLogH2Entity> h2Entity) {
        if (CollectionUtils.isEmpty(h2Entity)) {
            return new ArrayList<>();
        }

        return h2Entity.stream()
                .map(this::transferEntity)
                .collect(Collectors.toList());
    }

}
