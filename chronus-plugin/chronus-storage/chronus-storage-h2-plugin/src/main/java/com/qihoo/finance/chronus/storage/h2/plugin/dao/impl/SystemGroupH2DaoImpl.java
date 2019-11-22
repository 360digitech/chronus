package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.system.dao.SystemGroupDao;
import com.qihoo.finance.chronus.metadata.api.system.entity.SystemGroupEntity;

import java.util.List;
import java.util.Map;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class SystemGroupH2DaoImpl implements SystemGroupDao {

    @Override
    public void insert(SystemGroupEntity systemGroupEntity) {

    }

    @Override
    public void update(SystemGroupEntity systemGroupEntity) {

    }

    @Override
    public void delete(Object id) {

    }

    @Override
    public SystemGroupEntity selectByGroupName(String groupName, String sysCode) {
        return null;
    }

    @Override
    public List<SystemGroupEntity> selectListAll() {
        return null;
    }

    @Override
    public SystemGroupEntity loadSystemGroupBySysCode(String sysCode) {
        return null;
    }

    @Override
    public PageResult<SystemGroupEntity> findAllByPage(Integer page, Integer limit, Map<String, String> param) {
        return null;
    }
}
