package com.qihoo.finance.chronus.sdk.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface JobMethod {
    /**
     * Job定义的id 格式为 所在Bean名字.系统内唯一标识 如: XXXJob.deleteLog
     *
     * @return
     */
    String key();
}
