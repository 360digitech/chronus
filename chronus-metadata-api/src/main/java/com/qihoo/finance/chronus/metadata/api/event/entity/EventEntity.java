package com.qihoo.finance.chronus.metadata.api.event.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import java.util.Date;

/**
 * Created by xiongpu on 2019/11/2.
 */
@Getter
@Setter
public class EventEntity extends Entity {
    @Id
    private String id;

    private String cluster;
    /**
     * 机器
     */
    private String address;
    /**
     * 版本
     */
    private String version;
    /**
     * 事件编码
     */
    private String code;
    /**
     * 事件描述
     */
    private String desc;
    /**
     * 事件详情
     */
    private String message;

    private String content;

    private Long costTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    /* 非db字段 */
    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;

}
