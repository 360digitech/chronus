package com.qihoo.finance.chronus.core.group;

import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;

import java.util.List;

public interface GroupService {

	void insert(GroupEntity groupEntity);

	void update(GroupEntity groupEntity);

	void deleteByGroupName(String groupName);

	List<GroupEntity> loadAllGroup();

}
