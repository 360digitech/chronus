package com.qihoo.finance.chronus.metadata.api.group.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 分组
 */
@Getter
@Setter
public class GroupEntity extends Entity {

	@Id
	private String id;

	/**
	 * 分组名称
	 */
	@NotBlank
	private String groupName;

	@NotBlank
	private String groupDesc;

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dateCreated;

	private String createdBy = "sys";

	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date dateUpdated;

	private String updatedBy = "sys";

}
