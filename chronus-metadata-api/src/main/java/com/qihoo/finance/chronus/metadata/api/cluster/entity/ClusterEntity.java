package com.qihoo.finance.chronus.metadata.api.cluster.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import java.util.Date;

/**
 * Created by xiongpu on 2019/7/29.
 */
@Getter
@Setter
public class ClusterEntity extends Entity {

    @Id
    private String id;

    /**
     * 集群标识
     */
    @NotBlank
    private String cluster;

    /**
     * 描述
     */
    private String clusterDesc;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateCreated;

    private String createdBy = "sys";

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date dateUpdated;

    private String updatedBy = "sys";

}
