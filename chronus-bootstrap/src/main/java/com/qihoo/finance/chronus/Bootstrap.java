package com.qihoo.finance.chronus;

import com.qihoo.finance.chronus.common.ChronusConstants;
import com.qihoo.finance.chronus.common.NodeInfo;
import com.qihoo.finance.chronus.common.SupportConstants;
import com.qihoo.finance.chronus.common.ThreadFactory;
import com.qihoo.finance.chronus.common.job.AbstractTimerTask;
import com.qihoo.finance.chronus.registry.api.NamingService;
import com.qihoo.finance.chronus.support.Support;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import javax.annotation.Resource;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by xiongpu on 2019/7/28.
 */
@Slf4j
public class Bootstrap implements ApplicationListener<ContextRefreshedEvent> {
    private static final ScheduledExecutorService NODE_HEARTBEAT_SCHEDULE = Executors.newSingleThreadScheduledExecutor(new ThreadFactory(SupportConstants.SUPPORT_NAME_BOOTSTRAP, SupportConstants.MAIN));
    private volatile static boolean nodeStartFlag = false;
    @Autowired(required = false)
    private Map<String, Support> supports;

    @Resource
    private NamingService namingService;

    @Resource
    private NodeInfo currentNode;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (supports == null) {
            return;
        }
        try {
            NODE_HEARTBEAT_SCHEDULE.scheduleWithFixedDelay(heartbeatTask, 0, 3, TimeUnit.SECONDS);

            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                stop();
            }));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Runnable heartbeatTask = new AbstractTimerTask(SupportConstants.SUPPORT_NAME_BOOTSTRAP, "nodeHeartbeat", false) {
        @Override
        public void process() throws Exception {
            try {
                if (namingService.getCurrentNode() == null) {
                    registerNode();
                } else if (!namingService.currentNodeIsActive()) {
                    stopNode();
                } else if (namingService.currentNodeIsActive() && !nodeStartFlag) {
                    startNode();
                }
            } catch (Exception e) {
                log.error("节点心跳任务异常", e);
                stopNode();
            }
        }
    };


    private void stop() {
        try {
            NODE_HEARTBEAT_SCHEDULE.shutdown();
            stopNode();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void registerNode() throws Exception {
        if (Objects.equals(ChronusConstants.Y, currentNode.getEnableMaster()) || Objects.equals(ChronusConstants.Y, currentNode.getEnableWorker())) {
            namingService.registerNode();
            startNode();
        }
    }

    public void startNode() throws Exception {
        log.info("Bootstrap supports start ......");
        for (Map.Entry<String, Support> entry : supports.entrySet()) {
            entry.getValue().start();
        }
        nodeStartFlag = true;
        log.info("Bootstrap supports start end");
    }

    public void stopNode() throws Exception {
        try {
            if (supports == null) {
                return;
            }
            if (!nodeStartFlag) {
                return;
            }
            log.info("Bootstrap supports stop ......");
            for (Map.Entry<String, Support> entry : supports.entrySet()) {
                entry.getValue().stop();
            }
            log.info("Bootstrap supports stop end");
        } catch (Exception e) {

        }
        nodeStartFlag = false;
    }
}
