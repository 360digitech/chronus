package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.ClusterH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.util.EnvTestCaseUtil;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */
@DataJpaTest
@RunWith(SpringRunner.class)
public class ClusterJpaRepositoryTest {

    @Autowired
    private ClusterJpaRepository clusterJpaRepository;

    @Test
    public void findFirstByEnvAndAndCluster() {
        ClusterH2Entity envH2Entity = EnvTestCaseUtil.buildEnvH2One();
        ClusterH2Entity save = clusterJpaRepository.save(envH2Entity);
        ClusterH2Entity findOne = clusterJpaRepository.findFirstByAndCluster(envH2Entity.getCluster());
        Assert.assertNotNull(findOne);
        Assert.assertEquals(save,findOne);
        clusterJpaRepository.deleteById(save.getId());
    }
}