package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.SystemGroupH2Entity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午9:27:07
 */
@Repository
public interface SystemGroupJpaRepository extends JpaRepository<SystemGroupH2Entity, String> {

	@Query("select c from SystemGroupH2Entity c where c.groupName = :groupName and c.sysCode = :sysCode")
	SystemGroupH2Entity selectByGroupName(@Param("groupName") String groupName, @Param("sysCode") String sysCode);

	@Query("select c from SystemGroupH2Entity c where c.sysCode = :sysCode")
	SystemGroupH2Entity selectBySysCode(@Param("sysCode") String sysCode);

	@Query("select c from SystemGroupH2Entity c where c.groupName in(:groupNames) ")
	List<SystemGroupH2Entity> selectByGroupName(@Param("groupNames") List<String> groupNames);

	@Transactional
	@Modifying
	@Query(value = "update SystemGroupH2Entity c set c.groupName = :groupName , c.sysCode = :sysCode , c.sysDesc = :sysDesc , c.dateUpdated = :dateUpdated where c.id =:id")
	void update(@Param("groupName") String groupName, @Param("sysCode") String sysCode,
				@Param("sysDesc") String sysDesc, @Param("dateUpdated") Date dateUpdated, @Param("id") String id);

	@Query("select c from SystemGroupH2Entity c")
	Page<SystemGroupH2Entity> selectAllByPage(Pageable pageable);

}
