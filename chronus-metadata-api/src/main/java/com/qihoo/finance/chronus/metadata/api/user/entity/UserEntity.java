package com.qihoo.finance.chronus.metadata.api.user.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;
import java.util.List;

/**
 * @author chenxiyu
 * @date 2019/10/21
 */
@Getter
@Setter
public class UserEntity extends Entity {

    @Id
    private String id;

    private String userNo;

    private String name;

    private String pwd;

    /**
     * 以,分割开组别
     */
    private String group;

    private String roleNo;

    private String email;

    private String state;

    @Transient
    private String token;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";

    @Transient
    private String[] groups;
    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;
}
