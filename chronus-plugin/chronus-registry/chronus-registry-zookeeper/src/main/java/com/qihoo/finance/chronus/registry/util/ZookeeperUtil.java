package com.qihoo.finance.chronus.registry.util;

/**
 * Created by xiongpu on 2019/11/2.
 */
public class ZookeeperUtil {

    public static String getFullRootPath(String zkRootPath, String cluster) {
        return String.join("/", zkRootPath, "node", cluster);
    }

    public static String getFullNodePath(String fullRootPath, String address, String version) {
        return String.join("/", fullRootPath, String.join("$", address, version));
    }

}
