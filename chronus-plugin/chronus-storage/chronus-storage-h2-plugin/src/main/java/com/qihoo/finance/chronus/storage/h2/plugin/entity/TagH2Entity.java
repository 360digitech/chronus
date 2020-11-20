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
 * @date 2019年11月20日 下午4:33:38
 * @version 5.1.0
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.TAG_INFO)
public class TagH2Entity extends BaseH2Entity{
	private static final long serialVersionUID = 1L;
	
	@Id
    private String id;
    /**
     * tag名称
     */
    @Column(name = "tag")
    private String tag;
    
    /**
     * 备注
     */
    @Column(name = "remark")
    private String remark;

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
