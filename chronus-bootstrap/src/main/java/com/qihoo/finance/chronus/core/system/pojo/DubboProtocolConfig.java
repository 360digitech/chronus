package com.qihoo.finance.chronus.core.system.pojo;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Domain;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DubboProtocolConfig extends Domain {

    /**
     * dubbo注册中心地址
     * 默认为dubbo.registry.address配置的值
     */
    private String registryAddress = ChronusConstants.GLOBAL_REGISTRY_ADDRESS;

    private Integer timeout = 30000;
}
