package com.qihoo.finance.chronus.worker.entity;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiongpu on 2019/10/18.
 */
@Getter
@Setter
public class HttpJobConfig extends CommonConfig {

    /**
     * 远程服务地址
     * selectTasks: serverUrl/selectTasks
     * execute:serverUrl/selectTasks
     */
    private String serverUrl;
}
