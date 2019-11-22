package com.qihoo.finance.chronus.storage.h2.plugin.repository;

import com.qihoo.finance.chronus.storage.h2.plugin.entity.TaskRuntimeH2Entity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * @author jane.zhang
 * @Date 2019/10/22
 * @Description
 */
@Repository
public interface TaskRuntimeJpaRepository extends JpaRepository<TaskRuntimeH2Entity, Long> {
}
