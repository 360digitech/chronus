package com.qihoo.finance.chronus.storage.h2.plugin.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhangsi-pc.
 * @date 2019/9/21.
 */
public class TestCaseUtil {


    public static String object2JsonString(Object object,Class clazz,String... ignoreField){
        Field[] fields = clazz.getDeclaredFields();
        String[] fieldName = new String[fields.length-ignoreField.length];
        int i = 0;
        for(Field field:fields) {
            List<String> ignoreFields = Arrays.asList(ignoreField);
            if(!ignoreFields.contains(field.getName())) {
                fieldName[i++] = field.getName();
            }
        }
        SimplePropertyPreFilter filter = new SimplePropertyPreFilter(clazz, fieldName);
        return JSON.toJSONString(object,filter,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty);
    }
}
