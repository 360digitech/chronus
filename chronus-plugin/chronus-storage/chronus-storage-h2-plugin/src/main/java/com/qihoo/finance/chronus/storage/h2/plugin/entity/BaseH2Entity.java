package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.alibaba.fastjson.JSON;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
@Getter
@Setter
public class BaseH2Entity implements Serializable {

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
