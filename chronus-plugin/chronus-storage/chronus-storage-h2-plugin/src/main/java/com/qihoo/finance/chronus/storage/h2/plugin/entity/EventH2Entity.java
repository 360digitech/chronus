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
@Table(name = TableConstant.NODE_LOG_INFO)
public class EventH2Entity extends BaseH2Entity {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	/**
	 * 集群标识
	 */
	@Column(name = "cluster")
	private String cluster;

	/**
	 * 机器
	 */
	@Column(name = "address")
	private String address;
	
	/**
	 * 版本
	 */
	@Column(name = "version")
	private String version;
	
	/**
	 * 事件编码
	 */
	@Column(name = "code")
	private String code;
	
	/**
	 * 事件描述
	 */
	@Column(name = "desc")
	private String desc;
	
	/**
	 * 事件详情
	 */
	@Column(name = "message")
	private String message;

	@Column(name = "content")
	private String content;

	@Column(name = "cost_time")
	private Long costTime;

	@Column(name = "date_created")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dateCreated;

}
