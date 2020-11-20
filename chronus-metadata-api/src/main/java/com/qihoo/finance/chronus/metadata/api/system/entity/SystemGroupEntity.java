package com.qihoo.finance.chronus.metadata.api.system.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * 系统分组
 */
@Getter
@Setter
public class SystemGroupEntity extends Entity {

    @Id
    private String id;

    /**
     * 分组名称
     */
    @NotBlank
    private String groupName;

    @NotBlank
    private String sysCode;

    @NotBlank
    private String sysDesc;

    /**
     * 调用系统协议通道
     * DUBBO,HTTP
     */
    @NotBlank
    private String protocolType;

    @NotBlank
    private String protocolConfig;

    /* 非db字段 */
    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";

}
