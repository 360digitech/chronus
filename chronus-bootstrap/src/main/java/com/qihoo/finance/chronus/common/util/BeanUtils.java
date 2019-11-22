package com.qihoo.finance.chronus.common.util;


import com.qihoo.finance.chronus.common.exception.ServiceException;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xiongpu on 2017/11/23.
 */
public class BeanUtils {
    public static <T> T copyBean(Class<T> clazz, Object from){
        T to;
        try {
            to = clazz.newInstance();
            org.springframework.beans.BeanUtils.copyProperties(from, to);
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ServiceException(e);
        }
        return to;
    }

    public static <T> List<T> copyBeanList(Class<T> clazz, List<? extends Object> fromList){
        List<T> toList = new ArrayList<>();
        for(Object from : fromList) {
            toList.add(copyBean(clazz, from));
        }
        return toList;
    }
}
