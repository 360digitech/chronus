package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.SystemGroupH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.SystemGroupJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午9:22:49
 */
public class SystemGroupH2DaoImpl implements SystemGroupDao {

	@Autowired
	private SystemGroupJpaRepository systemGroupJpaRepository;

	private SystemGroupH2Entity transferH2Entity(SystemGroupEntity entity, boolean isNew) {
		if (null == entity) {
			return null;
		}
		SystemGroupH2Entity h2Entity = new SystemGroupH2Entity();
		BeanUtils.copyProperties(entity, h2Entity);
		if (isNew) {
			h2Entity.setId(H2IdUtil.getId());
			h2Entity.setDateCreated(new Date());
		}
		h2Entity.setDateUpdated(new Date());
		return h2Entity;
	}

	private SystemGroupEntity transferEntity(SystemGroupH2Entity h2Entity) {
		if (null == h2Entity) {
			return null;
		}
		SystemGroupEntity entity = new SystemGroupEntity();
		BeanUtils.copyProperties(h2Entity, entity);
		return entity;
	}

	@Override
	public void insert(SystemGroupEntity entity) {
		systemGroupJpaRepository.save(transferH2Entity(entity, true));
	}

	@Override
	public void update(SystemGroupEntity entity) {
		Optional<SystemGroupH2Entity> opt = systemGroupJpaRepository.findById(entity.getId());
		if (opt.isPresent()) {
			SystemGroupH2Entity dbObj = opt.get();
			SystemGroupH2Entity update = transferH2Entity(entity, false);
			update.setDateCreated(dbObj.getDateCreated());
			update.setCreatedBy(dbObj.getCreatedBy());
			systemGroupJpaRepository.save(update);
		}
	}

	@Override
	public void delete(Object id) {
		systemGroupJpaRepository.deleteById(String.valueOf(id));
	}

	@Override
	public SystemGroupEntity selectByGroupName(String groupName, String sysCode) {
		return transferEntity(systemGroupJpaRepository.selectByGroupName(groupName, sysCode));
	}

	@Override
	public List<SystemGroupEntity> selectListAll() {
		List<SystemGroupH2Entity> list = systemGroupJpaRepository.findAll();
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(this::transferEntity).collect(Collectors.toList());
	}

	@Override
	public SystemGroupEntity loadSystemGroupBySysCode(String sysCode) {
		return transferEntity(systemGroupJpaRepository.selectBySysCode(sysCode));
	}

	@Override
	public PageResult<SystemGroupEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
		SystemGroupH2Entity h2Entity = new SystemGroupH2Entity();
		h2Entity.setGroupName(param.get("groupName"));

		ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
				.withIgnoreCase(true).withMatcher("groupName", GenericPropertyMatchers.contains())
				.withIgnorePaths("id", "sysCode", "sysDesc")
				.withIgnorePaths("dateCreated", "createdBy", "dateUpdated", "updatedBy"); // 忽略非查询条件的属性

		// 创建实例
		Example<SystemGroupH2Entity> ex = Example.of(h2Entity, matcher);

		Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, limit);
		Page<SystemGroupH2Entity> pageList = systemGroupJpaRepository.findAll(ex, pageable);
		List<SystemGroupEntity> retList = new ArrayList<SystemGroupEntity>();
		if (!pageList.isEmpty()) {
			retList = pageList.getContent().stream().map(this::transferEntity).collect(Collectors.toList());
		}
		return new PageResult<SystemGroupEntity>(page, limit, pageList.getTotalElements(), pageList.getTotalPages(), retList);
	}

	@Override
	public List<SystemGroupEntity> selectSystemByGroupName(List<String> groupNames) {
		List<SystemGroupH2Entity> list = systemGroupJpaRepository.selectByGroupName(groupNames);
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(this::transferEntity).collect(Collectors.toList());
	}
}
