package com.qihoo.finance.chronus.metadata.api.log.dao;

import com.qihoo.finance.chronus.metadata.api.common.PageResult;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface NodeLogDao {
    void insert(NodeLogEntity nodeLogEntity);

    List<NodeLogEntity> selectListByClusterAndAddress(String cluster, String address);
}
