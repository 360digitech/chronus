package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.ClusterH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.ClusterJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;

/**
 * @author liuronghua
 * @date 2019年11月19日 下午8:01:26
 * @version 5.1.0
 */
public class ClusterH2DaoImpl implements ClusterDao {

	@Autowired
	private ClusterJpaRepository clusterJpaRepository;

	private ClusterH2Entity transferH2Entity(ClusterEntity entity) {
		if (null == entity) {
			return null;
		}
		ClusterH2Entity h2Entity = new ClusterH2Entity();
		BeanUtils.copyProperties(entity, h2Entity);
		h2Entity.setId(H2IdUtil.getId());
		h2Entity.setDateCreated(new Date());
		h2Entity.setDateUpdated(h2Entity.getDateCreated());
		return h2Entity;
	}

	private ClusterEntity transferEntity(ClusterH2Entity h2Entity) {
		if (null == h2Entity) {
			return null;
		}
		ClusterEntity entity = new ClusterEntity();
		BeanUtils.copyProperties(h2Entity, entity);
		return entity;
	}

	@Override
	public void insert(ClusterEntity clusterEntity) {
		clusterJpaRepository.save(transferH2Entity(clusterEntity));
	}

	@Override
	@Transactional
	public void update(ClusterEntity entity) {
		entity.setDateUpdated(new Date());
		clusterJpaRepository.updateEnvDesc(entity.getId(), entity.getClusterDesc());
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
		List<ClusterH2Entity> list = clusterJpaRepository.findAll();
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(this::transferEntity).collect(Collectors.toList());
	}

}
