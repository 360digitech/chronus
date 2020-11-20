package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.EventH2Entity;

/**
 * @author liuronghua
 * @date 2019年11月19日 下午8:17:33
 * @version 5.1.0
 */
@Repository
public interface EventJpaRepository extends JpaRepository<EventH2Entity, String> {

	@Query("select c from EventH2Entity c where c.cluster = :cluster and c.address = :address and c.version = :version")
	List<EventH2Entity> selectAll(@Param("cluster") String cluster, @Param("address") String address, @Param("version") String version);

	@Transactional
	@Modifying
	@Query(value = "update EventH2Entity c set c.cluster = :cluster , c.address = :address , c.version = :version where c.id =:id")
	void update(@Param("cluster") String cluster, @Param("address") String address, @Param("version") String version, @Param("id") String id);

}
