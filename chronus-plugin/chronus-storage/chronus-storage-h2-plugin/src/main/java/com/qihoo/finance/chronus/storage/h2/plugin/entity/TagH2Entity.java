package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.TAG_INFO)
public class TagH2Entity extends BaseH2Entity{
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    /**
     * tag名称
     */
    @Column(name = "tag")
    private String tag;
    /**
     * 备注
     */
    @Column(name = "remarks")
    private String remarks;

    /**
     * 分配的执行机器数量
     */
    @Column(name = "executor_num")
    private Integer executorNum;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";

}
