package com.qihoo.finance.chronus.core.cluster.service.impl;

import com.qihoo.finance.chronus.common.util.DateUtils;
import com.qihoo.finance.chronus.core.cluster.service.ClusterService;
import com.qihoo.finance.chronus.core.task.service.TaskService;
import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.metadata.api.task.entity.TaskEntity;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xiongpu on 2019/7/29.
 */
@Service
public class ClusterServiceImpl implements ClusterService {

    @Resource
    private ClusterDao clusterDao;

    @Resource
    private TaskService taskService;

    @Override
    public void insert(ClusterEntity clusterEntity) {
        if (clusterDao.selectByCluster(clusterEntity.getCluster()) != null) {
            throw new RuntimeException("Cluster:" + clusterEntity.getCluster() + ",已经存在,无法创建!");
        }
        clusterEntity.setDateCreated(DateUtils.now());
        clusterEntity.setDateUpdated(DateUtils.now());
        clusterDao.insert(clusterEntity);
    }

    @Override
    public void updateDesc(ClusterEntity clusterEntity) {
        clusterDao.updateDesc(clusterEntity);
    }

    @Override
    public void delete(String cluster) {
        List<TaskEntity> taskEntityList = taskService.selectTaskInfoByCluster(cluster);
        if (CollectionUtils.isNotEmpty(taskEntityList)) {
            throw new RuntimeException("Cluster:" + cluster + ",存在关联任务,无法删除!");
        }
        clusterDao.delete(cluster);
    }

    @Override
    public List<ClusterEntity> selectListAll() {
        return clusterDao.selectListAll();
    }
}
