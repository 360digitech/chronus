package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.log.dao.NodeLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.EventH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.EventJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午8:22:56
 */
public class NodeLogH2DaoImpl implements NodeLogDao {

    @Autowired
    private EventJpaRepository eventJpaRepository;

    private EventH2Entity transferH2Entity(NodeLogEntity entity, boolean isNew) {
        if (null == entity) {
            return null;
        }
        EventH2Entity h2Entity = new EventH2Entity();
        BeanUtils.copyProperties(entity, h2Entity);
        if (isNew) {
            h2Entity.setId(H2IdUtil.getId());
            h2Entity.setDateCreated(new Date());
        }
        return h2Entity;
    }

    private NodeLogEntity transferEntity(EventH2Entity h2Entity) {
        if (null == h2Entity) {
            return null;
        }
        NodeLogEntity entity = new NodeLogEntity();
        BeanUtils.copyProperties(h2Entity, entity);
        return entity;
    }

    @Override
    public void insert(NodeLogEntity entity) {
        eventJpaRepository.save(transferH2Entity(entity, true));
    }

    @Override
    public List<NodeLogEntity> selectListByClusterAndAddress(String cluster, String address) {
        return null;
    }
}
