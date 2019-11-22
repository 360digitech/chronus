package com.qihoo.finance.chronus.metadata.api.cluster.dao;

import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/9/13.
 */
public interface ClusterDao {
    /**
     * 插入集群信息 设置创建时间更新时间为当前时间
     * @param clusterEntity
     */
    void insert(ClusterEntity clusterEntity);

    /**
     * 更新集群描述
     * @param clusterEntity
     */
    void updateDesc(ClusterEntity clusterEntity);

    /**
     * 删除集群配置
     * @param cluster
     */
    void delete(String cluster);

    /**
     * 根据集群标识获取集群信息
     * @param cluster
     * @return
     */
    ClusterEntity selectByCluster(String cluster);

    /**
     * 获取所有的集群数据
     * @return
     */
    List<ClusterEntity> selectListAll();
}
