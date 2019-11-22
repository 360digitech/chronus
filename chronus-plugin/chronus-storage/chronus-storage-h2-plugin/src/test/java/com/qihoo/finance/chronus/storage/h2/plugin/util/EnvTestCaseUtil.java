package com.qihoo.finance.chronus.storage.h2.plugin.util;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.ClusterH2Entity;

import java.sql.Timestamp;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
public class EnvTestCaseUtil {
    public static ClusterH2Entity buildEnvH2One() {
        ClusterH2Entity envEntity = new ClusterH2Entity();
        envEntity.setCluster("A");
        envEntity.setClusterDesc("testA");
        envEntity.setCreatedBy("sys");
        envEntity.setDateCreated(new Timestamp(System.currentTimeMillis()));
        envEntity.setUpdatedBy("sys");
        envEntity.setDateUpdated(new Timestamp(System.currentTimeMillis()));
        return envEntity;
    }
}
