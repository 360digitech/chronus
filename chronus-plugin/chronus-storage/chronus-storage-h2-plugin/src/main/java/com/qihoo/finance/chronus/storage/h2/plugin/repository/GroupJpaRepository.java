package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.GroupH2Entity;
import com.qihoo.finance.chronus.storage.h2.plugin.entity.SystemGroupH2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午9:27:07
 */
@Repository
public interface GroupJpaRepository extends JpaRepository<GroupH2Entity, String> {

	@Query("select c from GroupH2Entity c where c.groupName = :groupName")
	GroupH2Entity selectByGroupName(@Param("groupName") String groupName);

	@Transactional
	@Modifying
	@Query(value = "delete from GroupH2Entity c where c.groupName =:groupName")
	void deleteByGroupName(@Param("groupName") String groupName);

}
