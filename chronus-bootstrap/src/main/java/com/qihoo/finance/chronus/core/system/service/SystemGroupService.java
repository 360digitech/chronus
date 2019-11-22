package com.qihoo.finance.chronus.core.system.service;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;

import java.util.List;
import java.util.Set;

/**
 * Created by xiongpu on 2018/11/2.
 */
public interface SystemGroupService {

    void insert(SystemGroupEntity systemGroupEntity);

    void update(SystemGroupEntity systemGroupEntity);

    void delete(SystemGroupEntity systemGroupEntity);

    Set<String> loadAllSysCodes() throws Exception;

    List<SystemGroupEntity> loadAllGroup();

    SystemGroupEntity loadSystemGroupBySysCode(String groupName);

    PageResult<SystemGroupEntity> selectListByPage(SystemGroupEntity systemGroupEntity);

    void createSystemGroupTrx(SystemGroupEntity systemGroupEntity) throws Exception;
}
