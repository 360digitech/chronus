package com.qihoo.finance.chronus.metadata.api.tag.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

/**
 * Created by xiongpu on 2019/8/14.
 */
@Getter
@Setter
public class TagEntity extends Entity {

    @Id
    private String id;

    /**
     * tag名称
     */
    private String tag;
    /**
     * 备注
     */
    private String remark;

    /**
     * 分配的执行机器数量
     */
    private Integer executorNum;


    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";


    @Transient
    private Integer pageSize;

    @Transient
    private Integer pageNum;
}
