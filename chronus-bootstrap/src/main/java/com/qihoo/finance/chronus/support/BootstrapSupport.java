package com.qihoo.finance.chronus.support;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.core.event.annotation.Event;
import com.qihoo.finance.chronus.core.event.enums.EventEnum;
import com.qihoo.finance.chronus.registry.api.NamingService;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Resource;
import java.util.Objects;

/**
 * Created by xiongpu on 2019/7/28.
 */
@Slf4j
public class BootstrapSupport implements Support {
    @Resource
    private NamingService namingService;

    @Resource
    private NodeInfo currentNode;

    @Override
    @Event(value = EventEnum.START_REGISTER, message = "注册完成!")
    public void start() throws Exception {
        if (Objects.equals(ChronusConstants.Y, currentNode.getEnableMaster()) || Objects.equals(ChronusConstants.Y, currentNode.getEnableExecutor())) {
            namingService.registerNode();
        }
    }

    @Override
    public void stop() {
    }
}
