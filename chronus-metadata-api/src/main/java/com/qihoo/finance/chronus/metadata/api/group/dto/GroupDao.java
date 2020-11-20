package com.qihoo.finance.chronus.metadata.api.group.dto;

import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;

import java.util.List;

public interface GroupDao {

	void insert(GroupEntity groupEntity);

	void update(GroupEntity groupEntity);

	void deleteByGroupName(String groupName);

	List<GroupEntity> selectListAll();

	GroupEntity selectByName(String name);

}
