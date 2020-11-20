package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import java.util.List;

import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.TagH2Entity;

/**
 * @author liuronghua
 * @date 2019年11月20日 下午4:24:37
 * @version 5.1.0
 */
@Repository
public interface TagJpaRepository extends JpaRepository<TagH2Entity, String> {

	@Query("select c from TagH2Entity c")
	Page<TagH2Entity> selectAllByPage(Example<TagH2Entity> ex, Pageable pageable);

	@Query("select c from TagH2Entity c where c.tag =:tag")
	TagH2Entity selectByTag(@Param("tag") String tag);
	
	@Query("select c from TagH2Entity c where c.tag in :tagNames")
	List<TagH2Entity> selectListByNames(@Param("tagNames") List<String> tagNames);

	@Transactional
	@Modifying
	@Query(value = "delete from TagH2Entity c where c.tag =:tag")
	void deleteByTag(@Param("tag") String tag);

}
