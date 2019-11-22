package com.qihoo.finance.chronus.common.util;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.groups.Default;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 校验工具类
 */
public class ValidationUtils {

    public static Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * 校验对象
     * （默认组）
     *
     * @param obj
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validateEntity(T obj) {
        return validateEntity(obj, Default.class);
    }

    /**
     * 校验对象列表
     *
     * @param objs
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validateList(List<T> objs) {
        ValidationResult result = new ValidationResult();
        result.setHasErrors(false);
        if (objs == null || objs.size() == 0) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<>();
            errorMsg.put("list", "入参为空");
            result.setErrorMsg(errorMsg);
            return result;
        }

        for (T obj : objs) {
            result = validateEntity(obj);
            if (result.isHasErrors()) {
                return result;
            }
        }

        return result;
    }


    /**
     * 校验对象属性
     *
     * @param obj
     * @param propertyName
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validateProperty(T obj, String propertyName) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> set = validator.validateProperty(obj, propertyName, Default.class);
        if (set != null && !set.isEmpty()) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<>();
            for (ConstraintViolation<T> cv : set) {
                errorMsg.put(propertyName, cv.getMessage());
            }
            result.setErrorMsg(errorMsg);
        }
        return result;
    }

    /**
     * 校验多个属性，有一个失败即为失败
     *
     * @param obj
     * @param propertyNames
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validatePropertys(T obj, String... propertyNames) {
        ValidationResult result = new ValidationResult();
        result.setHasErrors(false);
        if (propertyNames != null) {
            Set<ConstraintViolation<T>> set;
            for (String name : propertyNames) {
                set = validator.validateProperty(obj, name, Default.class);
                if (set != null && !set.isEmpty()) {
                    Map<String, String> errorMsg = new HashMap<>();
                    for (ConstraintViolation<T> cv : set) {
                        errorMsg.put(name, cv.getMessage());
                    }
                    result.setErrorMsg(errorMsg);
                    result.setHasErrors(true);
                    break;
                }
            }
        }

        return result;
    }

    /**
     * 校验对象列表 校验多个属性，有一个失败即为失败
     *
     * @param objs
     * @param propertyNames
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validateListPropertys(List<T> objs, String... propertyNames) {
        ValidationResult result = new ValidationResult();
        if (objs == null || objs.size() == 0) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<>();
            errorMsg.put("list", "入参为空");
            result.setErrorMsg(errorMsg);
            return result;
        }

        for (T obj : objs) {
            result = validatePropertys(obj, propertyNames);
            if (result.isHasErrors()) {
                return result;
            }
        }
        result.setHasErrors(true);
        Map<String, String> errorMsg = new HashMap<>();
        errorMsg.put("list", "入参为空");
        result.setErrorMsg(errorMsg);
        return result;
    }

    /**
     * 校验对象
     * （指定组）
     *
     * @param obj
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> ValidationResult validateEntity(T obj, Class... clazz) {
        ValidationResult result = new ValidationResult();
        Set<ConstraintViolation<T>> set = validator.validate(obj, clazz);
        if (set != null && !set.isEmpty()) {
            result.setHasErrors(true);
            Map<String, String> errorMsg = new HashMap<>();
            for (ConstraintViolation<T> cv : set) {
                errorMsg.put(cv.getPropertyPath().toString(), cv.getMessage());
            }
            result.setErrorMsg(errorMsg);
        }
        return result;
    }
}