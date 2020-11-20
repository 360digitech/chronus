package com.qihoo.finance.chronus.common.job;

import com.qihoo.finance.chronus.context.ServiceContextHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;

/**
 * Created by xiongpu on 2019/9/9.
 */
@Slf4j
public abstract class AbstractTimerTask implements Runnable {

    private String type;
    private String name;
    private boolean printCostTime;

    public AbstractTimerTask(String type, String name, boolean printCostTime) {
        this.type = type;
        this.name = name;
        this.printCostTime = printCostTime;
    }


    @Override
    public void run() {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        try {
            ServiceContextHelper.initContext();

            this.process();
        } catch (Throwable e) {
            log.error("{}:{} error", this.type, this.name, e);
        } finally {
            stopWatch.stop();
            if (this.printCostTime) {
                log.debug("{}:{} 执行完成,耗时:{}ms", this.type, this.name, stopWatch.getTime());
            }
        }
    }

    /**
     * 定时任务逻辑
     *
     * @throws Exception
     */
    public abstract void process() throws Exception;
}
