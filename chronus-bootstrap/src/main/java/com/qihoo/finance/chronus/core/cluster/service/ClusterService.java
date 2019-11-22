package com.qihoo.finance.chronus.core.cluster.service;

import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
public interface ClusterService {

    void insert(ClusterEntity clusterEntity);

    void updateDesc(ClusterEntity clusterEntity);

    void delete(String cluster);

    List<ClusterEntity> selectListAll();
}
