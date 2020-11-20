package com.qihoo.finance.chronus.core.group.impl;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.group.GroupService;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.group.dto.GroupDao;
import com.qihoo.finance.chronus.metadata.api.group.entity.GroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service("groupService")
public class GroupServiceImpl implements GroupService {

    @Resource
    private GroupDao groupDao;

    @Resource
    private SystemGroupService systemGroupService;

    @Override
    public void insert(GroupEntity groupEntity) {
        if (groupDao.selectByName(groupEntity.getGroupName()) != null) {
            throw new RuntimeException("GroupName:" + groupEntity.getGroupName() + ",已经存在,无法创建!");
        }
        groupEntity.setDateCreated(DateUtils.now());
        groupEntity.setDateUpdated(DateUtils.now());
        groupDao.insert(groupEntity);
    }

    @Override
    public void update(GroupEntity groupEntity) {
        groupDao.update(groupEntity);
    }

    @Override
    public void deleteByGroupName(String groupName) {
        // 还在引用，不能删
        if (CollectionUtils.isNotEmpty(systemGroupService.loadSystemGroupByGroupName(Arrays.asList(groupName)))) {
            throw new RuntimeException("GroupName:" + groupName + ",还存在关联系统,无法删除!");
        }

        groupDao.deleteByGroupName(groupName);
    }

    @Override
    public List<GroupEntity> loadAllGroup() {
        List<GroupEntity> groupEntityList = groupDao.selectListAll();
        return groupEntityList;
    }
}
