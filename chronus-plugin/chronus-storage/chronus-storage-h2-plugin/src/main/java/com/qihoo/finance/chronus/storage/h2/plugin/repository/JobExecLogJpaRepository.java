package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.JobExecLogH2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author zhangsi-pc.
 * @date 2019/10/18.
 */
@Repository
public interface JobExecLogJpaRepository extends JpaRepository<JobExecLogH2Entity, Long> {

    @Query("select c from JobExecLogH2Entity c where c.cluster =:cluster and c.taskName =:taskName and c.sysCode =:sysCode")
    List<JobExecLogH2Entity> selectListByCluster(@Param("cluster") String cluster,@Param("taskName") String taskName,@Param("sysCode") String sysCode);

}
