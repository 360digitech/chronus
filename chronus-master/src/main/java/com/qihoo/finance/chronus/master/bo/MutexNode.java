package com.qihoo.finance.chronus.master.bo;

import lombok.Getter;
import lombok.Setter;

/**
 * 互斥节点
 */
@Getter
@Setter
public class MutexNode {
    /**
     * 左节点
     */
    private String leftKey;

    /**
     * 关联key
     */
    private String relationKey;

    /**
     * 右节点
     */
    private String rightKey;

}
