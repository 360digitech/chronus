package com.qihoo.finance.chronus.metadata.api.common;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * @author chenxiyu
 * @date 2019/10/4
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private Integer pageNum;

    private Integer pageSize;

    private Long total;

    private Integer pages;

    private List<T> list;

}
