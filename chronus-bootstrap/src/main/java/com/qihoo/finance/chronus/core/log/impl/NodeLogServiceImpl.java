package com.qihoo.finance.chronus.core.log.impl;

import com.qihoo.finance.chronus.core.log.NodeLogBO;
import com.qihoo.finance.chronus.core.log.NodeLogService;
import com.qihoo.finance.chronus.metadata.api.log.dao.NodeLogDao;
import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by xiongpu on 2019/11/2.
 */
@Slf4j
public class NodeLogServiceImpl implements NodeLogService {

    @Resource
    private NodeLogDao nodeLogDao;

    @Override
    public void submitEvent(NodeLogBO event) {
        NodeLogEntity eventEntity = event.getEventEntity();
        nodeLogDao.insert(eventEntity);
    }

    @Override
    public List<NodeLogEntity> getLog(String cluster, String address) {
        return nodeLogDao.selectListByClusterAndAddress(cluster, address);
    }
}
