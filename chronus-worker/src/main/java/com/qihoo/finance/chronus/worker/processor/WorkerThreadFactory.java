package com.qihoo.finance.chronus.worker.processor;


import com.qihoo.finance.chronus.worker.service.ScheduleProcessor;

import java.util.concurrent.ThreadFactory;

/**
 * Created by xiongpu on 2020/7/27.
 */
public class WorkerThreadFactory implements ThreadFactory {

    @Override
    public Thread newThread(Runnable r) {
        if (r instanceof ScheduleProcessor) {
            return new Thread(r, ((ScheduleProcessor) r).getProcessorParam().getTaskItemEntity().getTaskItemId() + "-exe-main");
        }
        return new Thread(r);
    }
}
