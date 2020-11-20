package com.qihoo.finance.chronus.metadata.api.log.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;

import javax.validation.constraints.NotBlank;
import java.util.Date;
import java.util.List;

/**
 * 节点日志
 *
 * @author xiognpu
 */
@Getter
@Setter
public class NodeLogEntity extends Entity {

    @Id
    private String id;

    /**
     * 所属集群
     */
    private String cluster;

    private String address;

    private String tag;
    private String version;

    /**
     * 事件编码
     */
    private String code;
    /**
     * 事件描述
     */
    private String desc;

    private String message;

    private String content;
    private Long costTime;

    @Transient
    private Integer pageSize;
    @Transient
    private Integer pageNum;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;
}

