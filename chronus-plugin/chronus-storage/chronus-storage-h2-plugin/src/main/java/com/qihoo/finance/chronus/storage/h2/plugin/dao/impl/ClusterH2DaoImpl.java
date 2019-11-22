package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.ClusterH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.ClusterJpaRepository;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */
public class ClusterH2DaoImpl implements ClusterDao {

    @Autowired
    private ClusterJpaRepository clusterJpaRepository;

    @Override
    public void insert(ClusterEntity clusterEntity) {
        if (Objects.isNull(clusterEntity)) {
            throw new IllegalArgumentException("clusterEntity must not be null");
        }
        clusterJpaRepository.save(transferH2Entity(clusterEntity));
    }

    @Override
    @Transactional
    public void updateDesc(ClusterEntity clusterEntity) {
        if(Objects.isNull(clusterEntity)) {
            throw new IllegalArgumentException("envEntity must not be null");
        }
        clusterJpaRepository.updateEnvDesc(Long.valueOf(clusterEntity.getId()), clusterEntity.getClusterDesc());
    }

    @Override
    public void delete(String cluster) {
        clusterJpaRepository.deleteByCluster(cluster);
    }

    @Override
    public ClusterEntity selectByCluster(String cluster) {
        return transferEntity(clusterJpaRepository.findFirstByAndCluster(cluster));
    }

    @Override
    public List<ClusterEntity> selectListAll() {
        List<ClusterH2Entity> envH2Entities = clusterJpaRepository.findAll();
        if (CollectionUtils.isEmpty(envH2Entities)) {
            return new ArrayList<>();
        }

        return envH2Entities.stream().map(this::transferEntity).collect(Collectors.toList());
    }

    /**
     * id要重新手动设置一遍
     * @param envEntity
     * @return
     */
    private ClusterH2Entity transferH2Entity(ClusterEntity envEntity) {
        ClusterH2Entity envH2Entity = new ClusterH2Entity();
        BeanUtils.copyProperties(envEntity, envH2Entity);
        envH2Entity.setId(Long.valueOf(envEntity.getId()));
        return envH2Entity;
    }

    private ClusterEntity transferEntity(ClusterH2Entity envH2Entity) {
        ClusterEntity envEntity = new ClusterEntity();
        BeanUtils.copyProperties(envH2Entity, envEntity);
        envEntity.setId(String.valueOf(envH2Entity.getId()));
        return envEntity;
    }
}
