package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.JobExecLogH2Entity;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:36:46
 * @version 5.1.0
 */
@Repository
public interface JobExecLogJpaRepository extends JpaRepository<JobExecLogH2Entity, String> {

	@Query("select c from JobExecLogH2Entity c where c.cluster =:cluster and c.taskName =:taskName and c.sysCode =:sysCode")
	List<JobExecLogH2Entity> selectListByCluster(@Param("cluster") String cluster, @Param("taskName") String taskName,
			@Param("sysCode") String sysCode);

	@Query("select c from JobExecLogH2Entity c where c.cluster =:cluster and c.taskName =:taskName and c.sysCode in (:sysCodes)")
	Page<JobExecLogH2Entity> selectAllByPage(Pageable pageable, @Param("cluster") String cluster, @Param("taskName") String taskName,
			@Param("sysCodes") List<String> sysCodes);

}
