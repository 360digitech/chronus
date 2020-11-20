package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.dao.JobExecLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.JobExecLogEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.JobExecLogH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.JobExecLogJpaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.Date;
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

    private JobExecLogH2Entity transferH2Entity(JobExecLogEntity entity) {
        if (null == entity) {
            return null;
        }
        JobExecLogH2Entity h2Entity = new JobExecLogH2Entity();
        BeanUtils.copyProperties(entity, h2Entity);
        h2Entity.setDateCreated(new Date());
        h2Entity.setDateUpdated(h2Entity.getDateCreated());
        return h2Entity;
    }

    private JobExecLogEntity transferEntity(JobExecLogH2Entity h2Entity) {
        if (null == h2Entity) {
            return null;
        }
        JobExecLogEntity entity = new JobExecLogEntity();
        BeanUtils.copyProperties(h2Entity, entity);
        return entity;
    }

    @Override
    public void batchInsert(List<JobExecLogEntity> list) {
        if (CollectionUtils.isNotEmpty(list)) {
            List<JobExecLogH2Entity> h2list = list.stream().map(e -> transferH2Entity(e)).collect(Collectors.toList());
            jobExecLogJpaRepository.saveAll(h2list);
        }
    }

    @Override
    public List<JobExecLogEntity> selectListAll() {
        List<JobExecLogH2Entity> list = jobExecLogJpaRepository.findAll();
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::transferEntity).collect(Collectors.toList());

    }

    @Override
    public List<JobExecLogEntity> selectListByCluster(String cluster, String taskName, String sysCode) {
        List<JobExecLogH2Entity> list = jobExecLogJpaRepository.selectListByCluster(cluster, taskName, sysCode);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::transferEntity).collect(Collectors.toList());
    }

    @Override
    public PageResult<JobExecLogEntity> findAllByPage(JobExecLogEntity entity, List<String> sysCodes) {
        //TODO 待确认查询条件
        int page = entity.getPageNum();
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, entity.getPageSize());
        Page<JobExecLogH2Entity> pageList = jobExecLogJpaRepository.selectAllByPage(pageable, entity.getCluster(), entity.getTaskName(), sysCodes);
        List<JobExecLogEntity> retList = new ArrayList<JobExecLogEntity>();
        if (!pageList.isEmpty()) {
            retList = pageList.getContent().stream().map(this::transferEntity).collect(Collectors.toList());
        }
        return new PageResult<JobExecLogEntity>(entity.getPageNum(), entity.getPageSize(), pageList.getTotalElements(), pageList.getTotalPages(),
                retList);
    }

}
