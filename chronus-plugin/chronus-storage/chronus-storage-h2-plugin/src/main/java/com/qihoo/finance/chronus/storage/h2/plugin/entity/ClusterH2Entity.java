package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.CLUSTER_INFO)
public class ClusterH2Entity extends BaseH2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * 集群标识
     */
    @Column(name = "cluster")
    private String cluster;

    /**
     * 描述
     */
    @Column(name = "cluster_desc")
    private String clusterDesc;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";
}
