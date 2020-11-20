package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers;
import org.springframework.data.domain.ExampleMatcher.StringMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.tag.dao.TagDao;
import com.qihoo.finance.chronus.metadata.api.tag.entity.TagEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.TagH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.TagJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.H2IdUtil;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:24:12
 * @version 5.1.0
 */
public class TagH2DaoImpl implements TagDao {

	@Autowired
	private TagJpaRepository tagJpaRepository;

	private TagH2Entity transferH2Entity(TagEntity entity, boolean isNew) {
		if (null == entity) {
			return null;
		}
		TagH2Entity h2Entity = new TagH2Entity();
		BeanUtils.copyProperties(entity, h2Entity);
		if (isNew) {
			h2Entity.setId(H2IdUtil.getId());
			h2Entity.setDateCreated(new Date());
		}
		h2Entity.setDateUpdated(new Date());
		return h2Entity;
	}

	private TagEntity transferEntity(TagH2Entity h2Entity) {
		if (null == h2Entity) {
			return null;
		}
		TagEntity entity = new TagEntity();
		BeanUtils.copyProperties(h2Entity, entity);
		return entity;
	}

	@Override
	public void insert(TagEntity entity) {
		tagJpaRepository.save(transferH2Entity(entity, true));
	}

	@Override
	public void update(TagEntity entity) {
		Optional<TagH2Entity> opt = tagJpaRepository.findById(entity.getId());
		if (opt.isPresent()) {
			TagH2Entity dbObj = opt.get();
			TagH2Entity update = transferH2Entity(entity, false);
			update.setDateCreated(dbObj.getDateCreated());
			update.setCreatedBy(dbObj.getCreatedBy());
			tagJpaRepository.save(update);
		}
	}

	@Override
	public void delete(String tag) {
		tagJpaRepository.deleteByTag(tag);
	}

	@Override
	public TagEntity selectByTagName(String tag) {
		return transferEntity(tagJpaRepository.selectByTag(tag));
	}

	@Override
	public List<TagEntity> selectListAll() {
		List<TagH2Entity> list = tagJpaRepository.findAll();
		if (CollectionUtils.isEmpty(list)) {
			return new ArrayList<>();
		}
		return list.stream().map(this::transferEntity).collect(Collectors.toList());
	}

	@Override
	public PageResult<TagEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
		TagH2Entity h2Entity = new TagH2Entity();
		h2Entity.setRemark(param.get("remark"));
		h2Entity.setTag(param.get("tag"));

		ExampleMatcher matcher = ExampleMatcher.matching().withStringMatcher(StringMatcher.CONTAINING) // 改变默认字符串匹配方式：模糊查询
				.withIgnoreCase(true).withMatcher("remark", GenericPropertyMatchers.contains()).withMatcher("tag", GenericPropertyMatchers.contains())
				.withIgnorePaths("id").withIgnorePaths("dateCreated").withIgnorePaths("createdBy")
				.withIgnorePaths("dateUpdated").withIgnorePaths("updatedBy"); // 忽略非查询条件的属性

		// 创建实例
		Example<TagH2Entity> ex = Example.of(h2Entity, matcher);

		Pageable pageable = PageRequest.of(page > 0 ? page - 1 : page, limit);
		Page<TagH2Entity> pageList = tagJpaRepository.findAll(ex, pageable);
		List<TagEntity> retList = new ArrayList<TagEntity>();
		if (!pageList.isEmpty()) {
			retList = pageList.stream().map(this::transferEntity).collect(Collectors.toList());
		}
		return new PageResult<TagEntity>(page, limit, pageList.getTotalElements(), pageList.getTotalPages(), retList);
	}
}
