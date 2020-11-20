package com.qihoo.finance.chronus.worker.log;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface JobInvokeMonitor {
}
