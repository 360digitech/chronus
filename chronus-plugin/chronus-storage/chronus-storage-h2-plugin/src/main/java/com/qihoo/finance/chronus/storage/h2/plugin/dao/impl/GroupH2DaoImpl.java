package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.group.dto.GroupDao;
import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.GroupH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.GroupJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午9:22:49
 */
public class GroupH2DaoImpl implements GroupDao {

	@Resource
	private GroupJpaRepository groupJpaRepository;

	private GroupH2Entity transferH2Entity(GroupEntity entity, boolean isNew) {
		if (null == entity) {
			return null;
		}
		GroupH2Entity h2Entity = new GroupH2Entity();
		BeanUtils.copyProperties(entity, h2Entity);
		if (isNew) {
			h2Entity.setId(H2IdUtil.getId());
			h2Entity.setDateCreated(new Date());
		}
		h2Entity.setDateUpdated(new Date());
		return h2Entity;
	}

	private GroupEntity transferEntity(GroupH2Entity h2Entity) {
		if (null == h2Entity) {
			return null;
		}
		GroupEntity entity = new GroupEntity();
		BeanUtils.copyProperties(h2Entity, entity);
		return entity;
	}

	@Override
	public void insert(GroupEntity groupEntity) {
		groupJpaRepository.save(transferH2Entity(groupEntity, true));
	}

	@Override
	public void update(GroupEntity groupEntity) {
		Optional<GroupH2Entity> opt = groupJpaRepository.findById(groupEntity.getId());
		if (opt.isPresent()) {
			GroupH2Entity dbObj = opt.get();
			GroupH2Entity update = transferH2Entity(groupEntity, false);
			update.setDateCreated(dbObj.getDateCreated());
			update.setCreatedBy(dbObj.getCreatedBy());
			groupJpaRepository.save(update);
		}
	}

	@Override
	public void deleteByGroupName(String groupName) {
		groupJpaRepository.deleteByGroupName(groupName);
	}

	@Override
	public List<GroupEntity> selectListAll() {
		List<GroupH2Entity> list = groupJpaRepository.findAll();
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(this::transferEntity).collect(Collectors.toList());
	}

	@Override
	public GroupEntity selectByName(String name) {
		return transferEntity(groupJpaRepository.selectByGroupName(name));
	}
}
