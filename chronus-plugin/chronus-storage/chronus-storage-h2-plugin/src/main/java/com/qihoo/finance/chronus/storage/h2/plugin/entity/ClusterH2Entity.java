package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;

import lombok.Getter;
import lombok.Setter;

/**
 * @author liuronghua
 * @date 2019年11月19日 下午8:01:26
 * @version 5.1.0
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.CLUSTER_INFO)
public class ClusterH2Entity extends BaseH2Entity {
	private static final long serialVersionUID = 1L;

	@Id
	// @GeneratedValue(strategy = GenerationType.AUTO)
	// @Column(name = "id")
	private String id;

	/**
	 * 集群标识
	 */
	@Column(name = "cluster")
	private String cluster;

	/**
	 * 描述
	 */
	@Column(name = "cluster_desc")
	private String clusterDesc;

	@Column(name = "date_created")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dateCreated;

	@Column(name = "created_by")
	private String createdBy = "sys";

	@Column(name = "date_updated")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dateUpdated;

	@Column(name = "updated_by")
	private String updatedBy = "sys";
}
