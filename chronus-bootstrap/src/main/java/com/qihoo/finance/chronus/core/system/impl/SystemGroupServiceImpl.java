package com.qihoo.finance.chronus.core.system.impl;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.system.SystemGroupService;
import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by xiongpu on 2018/11/2.
 */
@Slf4j
@Service("systemGroupService")
public class SystemGroupServiceImpl implements SystemGroupService {

    @Resource
    private SystemGroupDao systemGroupDao;

    @Override
    public void insert(SystemGroupEntity systemGroupEntity) {
        if (systemGroupDao.selectByGroupName(systemGroupEntity.getGroupName(), systemGroupEntity.getSysCode()) != null) {
            throw new RuntimeException("GroupName:" + systemGroupEntity.getGroupName() + ":" + systemGroupEntity.getSysCode() + ",已经存在,无法创建!");
        }
        systemGroupEntity.setDateCreated(DateUtils.now());
        systemGroupEntity.setDateUpdated(DateUtils.now());
        systemGroupDao.insert(systemGroupEntity);
    }

    @Override
    public void update(SystemGroupEntity systemGroupEntity) {
        systemGroupDao.update(systemGroupEntity);
    }

    @Override
    public void delete(SystemGroupEntity systemGroupEntity) {
        systemGroupDao.delete(systemGroupEntity.getId());
    }


    @Override
    public Set<String> loadAllSysCodes() {
        List<SystemGroupEntity> systemStrategyGroups = systemGroupDao.selectListAll();
        if (CollectionUtils.isEmpty(systemStrategyGroups)) {
            return new HashSet<>();
        }
        List<String> groupNames = systemStrategyGroups.stream().map(SystemGroupEntity::getSysCode).collect(Collectors.toList());
        Collections.sort(groupNames);
        Set<String> allGroup = new LinkedHashSet<>(groupNames);
        return allGroup;
    }

    @Override
    public SystemGroupEntity loadSystemGroupBySysCode(String sysCode) {
        return systemGroupDao.loadSystemGroupBySysCode(sysCode);
    }

    @Override
    public PageResult<SystemGroupEntity> selectListByPage(SystemGroupEntity systemGroupEntity) {
        Map<String, String> param = new HashMap<>(1);
        param.put("groupName", systemGroupEntity.getGroupName());
        param.put("sysCode", systemGroupEntity.getSysCode());
        return systemGroupDao.findAllByPage(systemGroupEntity.getPageNum(), systemGroupEntity.getPageSize(), param);
    }

    @Override
    public List<SystemGroupEntity> loadSystemGroupByGroupName(List<String> groupNames) {
        return systemGroupDao.selectSystemByGroupName(groupNames);
    }
}
