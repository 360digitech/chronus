package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * @author liuronghua
 * @version 5.1.0
 * @date 2019年11月19日 下午9:26:44
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.SYSTEM_GROUP_INFO)
public class SystemGroupH2Entity extends BaseH2Entity {
	private static final long serialVersionUID = 1L;

	@Id
	private String id;

	/**
	 * 分组名称
	 */
	@NotBlank
	@Column(name = "group_name")
	private String groupName;

	@NotBlank
	@Column(name = "sys_code")
	private String sysCode;

	@NotBlank
	@Column(name = "sys_desc")
	private String sysDesc;

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

	/**
	 * 非db字段
	 */
	@Transient
	private Integer pageSize;

	@Transient
	private Integer pageNum;
}
