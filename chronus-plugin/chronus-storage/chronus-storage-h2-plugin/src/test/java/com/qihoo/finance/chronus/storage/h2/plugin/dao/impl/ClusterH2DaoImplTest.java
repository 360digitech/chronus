package com.qihoo.finance.chronus.storage.h2.plugin.dao.impl;

import com.alibaba.fastjson.JSON;
import com.qihoo.finance.chronus.metadata.api.cluster.dao.ClusterDao;
import com.qihoo.finance.chronus.metadata.api.cluster.entity.ClusterEntity;
import com.qihoo.finance.chronus.storage.h2.plugin.TestApplication;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.ClusterH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.repository.ClusterJpaRepository;
import com.qihoo.finance.chronus.storage.h2.plugin.util.EnvTestCaseUtil;
import com.qihoo.finance.chronus.storage.h2.plugin.util.TestCaseUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {TestApplication.class})
@Slf4j
public class ClusterH2DaoImplTest {

    @Autowired
    private ClusterDao clusterDao;

    @Autowired
    private ClusterJpaRepository clusterJpaRepository;

    @Test
    public void insert() {
        ClusterH2Entity envH2Entity = insertOneData();
        List<ClusterEntity> envEntities = clusterDao.selectListAll();
        log.info("[h2 env dao] save data,envEntities:{}", JSON.toJSONString(envEntities));
        Assert.assertNotNull(envEntities);
        Assert.assertTrue(CollectionUtils.isNotEmpty(envEntities));
        clusterJpaRepository.deleteById(envH2Entity.getId());
    }

    @Test
    @Rollback
    public void updateDesc() {
        ClusterH2Entity envH2Entity = insertOneData();
        ClusterEntity envEntity = new ClusterEntity();
        envEntity.setId(String.valueOf(envH2Entity.getId()));
        envEntity.setClusterDesc("update desc");
        clusterDao.updateDesc(envEntity);

        List<ClusterEntity> envEntities = clusterDao.selectListAll();
        Assert.assertNotNull(envEntities);
        Assert.assertTrue(CollectionUtils.isNotEmpty(envEntities));
        ClusterEntity updatedEntity = envEntities.stream().findFirst().get();
        Assert.assertEquals("update desc unsuccess", envEntity.getClusterDesc(), updatedEntity.getClusterDesc());
        clusterJpaRepository.deleteById(envH2Entity.getId());
    }


    @Test
    public void selectByCluster() {
        ClusterH2Entity envH2Entity = insertOneData();
        ClusterEntity envEntity = clusterDao.selectByCluster(envH2Entity.getCluster());
        Assert.assertNotNull(envEntity);
        assertEqualWithIgnore(envH2Entity, envEntity, "id");
        clusterJpaRepository.deleteById(envH2Entity.getId());
    }


    private ClusterH2Entity insertOneData() {
        ClusterH2Entity envH2Entity = EnvTestCaseUtil.buildEnvH2One();
        return clusterJpaRepository.save(envH2Entity);
    }

    private void assertEqualWithIgnore(ClusterH2Entity expected, ClusterEntity actual, String... ignoreField) {
        Assert.assertEquals(TestCaseUtil.object2JsonString(expected, ClusterH2Entity.class, ignoreField),
                TestCaseUtil.object2JsonString(actual, ClusterEntity.class, ignoreField));
    }
}