package com.qihoo.finance.chronus.executor.processor;

import com.qihoo.finance.chronus.executor.bo.ProcessorParam;
import com.qihoo.finance.chronus.executor.service.ScheduleProcessor;

/**
 * Created by xiongpu on 2019/9/17.
 */
public class ProcessorFactory {
    private static String PROCESSOR_TYPE_SLEEP = "SLEEP";
    private static String PROCESSOR_TYPE_SIMPLE = "SIMPLE";

    public static ScheduleProcessor create(String type, ProcessorParam processorParam) {
        if (PROCESSOR_TYPE_SLEEP.equals(type)) {
            return new ScheduleSleepProcessor(processorParam);
        } else if (PROCESSOR_TYPE_SIMPLE.equals(type)) {
            return new ScheduleSimpleProcessor(processorParam);
        }
        throw new RuntimeException("Unsupported Task Type:" + type);
    }
}
