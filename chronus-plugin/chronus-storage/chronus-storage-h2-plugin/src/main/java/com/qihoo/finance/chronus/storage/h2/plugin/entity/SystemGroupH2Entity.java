package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Transient;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.sql.Timestamp;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.SYSTEM_GROUP_INFO)
public class SystemGroupH2Entity extends BaseH2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 分组名称
     */
    @NotBlank
    @Column(name = "group_name")
    private String groupName = "PUBLIC";

    @NotBlank
    @Column(name = "sys_code")
    private String sysCode;

    @NotBlank
    @Column(name = "group_desc")
    private String groupDesc;

    @NotBlank
    @Column(name = "sys_desc")
    private String sysDesc;

    /**
     * 非db字段
     */
    @Transient
    private Integer pageSize;

    @Transient
    private Integer pageNum;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";
}
