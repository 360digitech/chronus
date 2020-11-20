package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:36:10
 * @version 5.1.0
 */
@Repository
public interface TaskRuntimeJpaRepository extends JpaRepository<TaskRuntimeH2Entity, String> {

	@Query("select c from TaskRuntimeH2Entity c where c.cluster = :cluster and c.taskName = :taskName")
	List<TaskRuntimeH2Entity> selectByClusterAndTaskName(@Param("cluster") String cluster, @Param("taskName") String taskName);

}
