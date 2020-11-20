package com.qihoo.finance.chronus.worker.processor;

import com.qihoo.finance.chronus.context.SpringContextHolder;
import com.qihoo.finance.chronus.worker.service.ScheduleProcessor;

/**
 * Created by xiongpu on 2019/9/17.
 */
public class ProcessorFactory {
    
    public static ScheduleProcessor create(String type) {
        Object process;
        try {
            process = SpringContextHolder.getBean(type);
        } catch (Exception e) {
            throw e;
        }

        if (!(process instanceof ScheduleProcessor)) {
            throw new RuntimeException("Unsupported Task Type:" + type);
        }
        return ((ScheduleProcessor) process);
    }
}
