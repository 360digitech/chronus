package com.qihoo.finance.chronus.metadata.api.assign.entity;

import com.qihoo.finance.chronus.metadata.api.common.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by xiongpu on 2019/10/27.
 */
@Getter
@Setter
public class TaskAssignResultEntity extends Entity {

    private String masterVersion;
    private String cluster;
    private String executorAddress;
    private Integer seqNo;
    private String taskName;
    private Integer assignNum;
    private String taskItems;
    private String state;

}
