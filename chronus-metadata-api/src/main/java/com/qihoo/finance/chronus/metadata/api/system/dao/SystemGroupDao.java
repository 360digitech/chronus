package com.qihoo.finance.chronus.metadata.api.system.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface SystemGroupDao {

    void insert(SystemGroupEntity systemGroupEntity);

    void update(SystemGroupEntity systemGroupEntity);

    void delete(Object id);

    SystemGroupEntity selectByGroupName(String groupName, String sysCode);

    List<SystemGroupEntity> selectListAll();

    SystemGroupEntity loadSystemGroupBySysCode(String sysCode);

    PageResult<SystemGroupEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param);
}
