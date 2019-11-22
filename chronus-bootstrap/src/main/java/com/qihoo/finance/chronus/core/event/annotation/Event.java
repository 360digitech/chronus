package com.qihoo.finance.chronus.core.event.annotation;

import com.qihoo.finance.chronus.core.event.enums.EventEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by xiongpu on 2019/11/2.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Event {

    EventEnum value();

    String message() default "";

    boolean resultPutContent() default false;
}
