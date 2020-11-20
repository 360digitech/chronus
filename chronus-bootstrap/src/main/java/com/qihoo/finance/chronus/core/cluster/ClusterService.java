package com.qihoo.finance.chronus.core.cluster;

import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
public interface ClusterService {

    void insert(ClusterEntity clusterEntity);

    void update(ClusterEntity clusterEntity);

    void delete(String cluster);
    
    ClusterEntity selectByCluster(String cluster);

    List<ClusterEntity> selectListAll();

    void dataGuardStart(String dataGuardCluster);

    void dataGuardStop(String dataGuardCluster);
}
