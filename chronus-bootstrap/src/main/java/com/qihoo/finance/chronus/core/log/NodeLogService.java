package com.qihoo.finance.chronus.core.log;

import com.qihoo.finance.chronus.metadata.api.log.entity.NodeLogEntity;

import java.util.List;

/**
 * Created by xiongpu on 2019/11/2.
 */
public interface NodeLogService {
    /**
     * 提交事件到数据库
     *
     * @param event
     */
    void submitEvent(NodeLogBO event);

    List<NodeLogEntity> getLog(String cluster, String address);
}
