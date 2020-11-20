package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.TaskH2Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月20日 下午4:36:23
 */
@Repository
public interface TaskJpaRepository extends JpaRepository<TaskH2Entity, String> {

	@Query("select c from TaskH2Entity c where c.taskName =:taskName and c.cluster =:cluster and c.state =:state and c.dealSysCode in (:dealSysCodes)")
	Page<TaskH2Entity> selectAllByPage(@Param("dealSysCodes")List<String> dealSysCodes, @Param("cluster")String cluster, @Param("state")String state, @Param("taskName")String taskName, Pageable pageable);

    @Query("select c from TaskH2Entity c where c.cluster = :cluster and c.taskName = :taskName")
    TaskH2Entity selectByTaskName(@Param("cluster") String cluster, @Param("taskName") String taskName);

    @Query("select c from TaskH2Entity c where c.cluster = :cluster")
    List<TaskH2Entity> selectByCluster(@Param("cluster") String cluster);

    @Query("select c from TaskH2Entity c where c.tag = :tag")
    List<TaskH2Entity> selectByTag(@Param("tag") String tag);

    @Transactional
    @Modifying
    @Query(value = "delete from TaskH2Entity c where c.cluster = :cluster and c.taskName =:taskName")
    void deleteByTaskName(@Param("cluster") String cluster, @Param("taskName") String taskName);
}
