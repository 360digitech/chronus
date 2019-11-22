package com.qihoo.finance.chronus.storage.h2.plugin.entity;

import com.qihoo.finance.chronus.metadata.api.common.TableConstant;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

/**
 * @author jane.zhang
 * @Date 2019/9/22
 * @Description
 */
@Getter
@Setter
@Entity
@Table(name = TableConstant.TAG_ASSIGN_INFO)
public class TagAssignH2Entity extends BaseH2Entity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "cluster")
    private String cluster;

    /**
     * tag名称
     */
    @Column(name = "tag")
    private String tag;

    /**
     * 分配的地址列表
     */
    @Column(name = "address_list")
    private String addressList;

    @Column(name = "date_created")
    private Timestamp dateCreated;

    @Column(name = "created_by")
    private String createdBy = "sys";

    @Column(name = "date_updated")
    private Timestamp dateUpdated;

    @Column(name = "updated_by")
    private String updatedBy = "sys";

    public List getAddressList() {
        return Arrays.asList(StringUtils.split(addressList,","));
    }
}
