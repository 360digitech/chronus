package com.qihoo.finance.chronus.common.domain;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;

/**
 * Created by xiongpu on 2017/9/25.
 */
public class Domain implements Serializable{

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
