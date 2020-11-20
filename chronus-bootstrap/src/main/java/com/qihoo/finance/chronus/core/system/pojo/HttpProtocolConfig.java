package com.qihoo.finance.chronus.core.system.pojo;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.domain.Domain;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class HttpProtocolConfig extends Domain {

    /**
     * 服务地址
     */
    private String serviceUrl = ChronusConstants.GLOBAL_SERVICE_URL;

    private Integer timeout = 30000;

    /**
     * 请求头
     */
    private Map<String, String> header;
}
