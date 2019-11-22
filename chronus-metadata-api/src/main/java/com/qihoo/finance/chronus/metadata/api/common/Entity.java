package com.qihoo.finance.chronus.metadata.api.common;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

/**
 * Created by xiongpu on 17/6/27.
 */
public class Entity implements Serializable {


    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
