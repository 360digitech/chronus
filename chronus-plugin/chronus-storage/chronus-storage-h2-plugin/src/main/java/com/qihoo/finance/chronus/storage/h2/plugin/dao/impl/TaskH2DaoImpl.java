package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.task.dao.TaskDao;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.TaskH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.TaskJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月20日 下午4:38:52
 */
public class TaskH2DaoImpl implements TaskDao {

    @Autowired
    private TaskJpaRepository taskJpaRepository;

    private TaskH2Entity transferH2Entity(TaskEntity entity, boolean isNew) {
        if (null == entity) {
            return null;
        }
        TaskH2Entity h2Entity = new TaskH2Entity();
        BeanUtils.copyProperties(entity, h2Entity);
        if (isNew) {
            h2Entity.setId(H2IdUtil.getId());
            h2Entity.setDateCreated(new Date());
        }
        h2Entity.setDateUpdated(new Date());
        return h2Entity;
    }

    private TaskEntity transferEntity(TaskH2Entity h2Entity) {
        if (null == h2Entity) {
            return null;
        }
        TaskEntity entity = new TaskEntity();
        BeanUtils.copyProperties(h2Entity, entity);
        return entity;
    }

    @Override
    public void insert(TaskEntity entity) {
        taskJpaRepository.save(transferH2Entity(entity, true));
    }

    @Override
    public void update(TaskEntity entity) {
        Optional<TaskH2Entity> opt = taskJpaRepository.findById(entity.getId());
        if (opt.isPresent()) {
            TaskH2Entity dbObj = opt.get();
            TaskH2Entity update = transferH2Entity(entity, false);
            update.setDateCreated(dbObj.getDateCreated());
            update.setCreatedBy(dbObj.getCreatedBy());
            taskJpaRepository.save(update);
        }
    }

    @Override
    public TaskEntity selectById(String id) {
        Optional<TaskH2Entity> h2Entity = taskJpaRepository.findById(id);
        return h2Entity.isPresent() ? transferEntity(h2Entity.get()) : null;
    }


    @Override
    public PageResult<TaskEntity> selectListByPage(TaskEntity taskEntity, List<String> dealSysCodes) {
        Integer page = taskEntity.getPageNum();
        Integer limit = taskEntity.getPageSize();
        Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, limit);
        Page<TaskH2Entity> pageList = taskJpaRepository.selectAllByPage(dealSysCodes, taskEntity.getCluster(), taskEntity.getState(), taskEntity.getTaskName(), pageable);
        List<TaskEntity> retList = new ArrayList<TaskEntity>();
        if (!pageList.isEmpty()) {
            retList = pageList.getContent().stream().map(this::transferEntity).collect(Collectors.toList());
        }
        return new PageResult<>(page, limit, pageList.getTotalElements(), pageList.getTotalPages(), retList);
    }

    @Override
    public List<TaskEntity> selectTaskInfoByCluster(String cluster) {
        List<TaskH2Entity> list = taskJpaRepository.selectByCluster(cluster);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::transferEntity).collect(Collectors.toList());
    }


    @Override
    public void delete(String taskName) {

    }

    @Override
    public TaskEntity selectTaskInfoByTaskName(String taskName) {
        return null;
    }

    @Override
    public List<TaskEntity> selectAllTaskInfo() {
        return null;
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTaskNames(Collection<String> taskNameSet) {
        return null;
    }

    @Override
    public List<TaskEntity> selectTaskInfoByTag(String tag) {
        List<TaskH2Entity> list = taskJpaRepository.selectByTag(tag);
        if (CollectionUtils.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(this::transferEntity).collect(Collectors.toList());
    }
}
