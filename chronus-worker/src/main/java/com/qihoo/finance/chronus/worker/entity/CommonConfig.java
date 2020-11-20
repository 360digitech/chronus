package com.qihoo.finance.chronus.worker.entity;

import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiongpu on 2019/10/18.
 */
@Getter
@Setter
public class CommonConfig extends Entity {

    /**
     * 任务bean的参数，由用户自定义格式的字符串
     */
    private String taskParameter;

}

