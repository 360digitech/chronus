package com.qihoo.finance.chronus.registry.util;

import com.alibaba.nacos.api.naming.pojo.Instance;

/**
 * Created by xiongpu on 2019/9/22.
 */
public class InstanceUtils {
    public static String getAddressByInstance(Instance instance) {
        return instance.getIp() + ":" + instance.getPort();
    }
}
