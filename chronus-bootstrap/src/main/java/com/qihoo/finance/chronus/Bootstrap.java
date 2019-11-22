package com.qihoo.finance.chronus;

import com.qihoo.finance.chronus.support.Support;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.Map;

/**
 * Created by xiongpu on 2019/7/28.
 */
@Slf4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired(required = false)
    private Map<String, Support> supports;

    public void start() throws Exception {
        log.info("Bootstrap supports start ......");
        for (Map.Entry<String, Support> entry : supports.entrySet()) {
            entry.getValue().start();
        }
        log.info("Bootstrap supports start end");
    }

    public void stop() throws Exception {
        if (supports == null) {
            return;
        }
        log.info("Bootstrap supports stop ......");
        for (Map.Entry<String, Support> entry : supports.entrySet()) {
            entry.getValue().stop();
        }
        log.info("Bootstrap supports stop end");
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (supports == null) {
            return;
        }
        try {
            this.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
