package com.qihoo.finance.chronus.core.log;

import com.qihoo.finance.chronus.core.log.pojo.LogConfig;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by xiongpu on 2017/7/3.
 */
public class LogFactory {
    private static LogFactory logFactory = new LogFactory();
    protected Lock lock = new ReentrantLock();

    private LogFactory() {
    }

    public static LogFactory getLogFactory() {
        return logFactory;
    }


    public void start(LogConfig abyLogConfig) {
        //为false时，返回多个LoggerContext对象， true：返回唯一的单例LoggerContext
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        org.apache.logging.log4j.core.Logger cfgLogger = (org.apache.logging.log4j.core.Logger) LogManager.getLogger(abyLogConfig.getCfgLogName());
        if (cfgLogger == null) {
            LogManager.getLogger(LogFactory.class).error("not found {} logger (please define in log4j2.xml)", abyLogConfig.getCfgLogName());
            return;
        }

        List<AppenderRef> appenderRefs = new ArrayList<>();
        Map<String, Appender> appenderMap = cfgLogger.getAppenders();
        List<Appender> appenders = new ArrayList<>();
        appenderMap.forEach((key, appenderCfg) -> {
            Appender appender;
            if (appenderCfg instanceof ConsoleAppender) {
                appender = appenderCfg;
            } else {
                appender = getAppender(abyLogConfig, config, appenderCfg);
            }

            if (appender != null) {
                AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), Level.ALL, null);
                appenderRefs.add(ref);
                appenders.add(appender);
            }
        });
        if (CollectionUtils.isEmpty(appenders)) {
            return;
        }
        AppenderRef[] refs = new AppenderRef[appenderRefs.size()];
        refs = appenderRefs.toArray(refs);
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, cfgLogger.getLevel(), abyLogConfig.getName(),
                "true", refs, null, config, null);
        appenders.stream().forEach(e ->
                loggerConfig.addAppender(e, Level.ALL, null)
        );
        config.addLogger(abyLogConfig.getName(), loggerConfig);
        ctx.updateLoggers(config);
    }


    private Appender getAppender(LogConfig abyLogConfig, Configuration config, Appender appenderCfg) {
        Appender appender = null;
        if (appenderCfg instanceof RollingFileAppender) {
            RollingFileAppender rollingFileAppender = (RollingFileAppender) appenderCfg;
            appender = RollingFileAppender.newBuilder()
                    .setConfiguration(config)
                    .withName(appenderCfg.getName().replace(abyLogConfig.getAppenderNameKey(), abyLogConfig.getName()))
                    .withFileName(rollingFileAppender.getFileName().replaceAll(abyLogConfig.getFileNameKey(), abyLogConfig.getFileName()))
                    .withFilePattern(rollingFileAppender.getFilePattern().replaceAll(abyLogConfig.getFileNameKey(), abyLogConfig.getFileName()))
                    .withLayout(rollingFileAppender.getLayout())
                    .withFilter(rollingFileAppender.getFilter())
                    .withPolicy(rollingFileAppender.getTriggeringPolicy())
                    .withStrategy(rollingFileAppender.getManager().getRolloverStrategy())
                    .build();
        } else if (appenderCfg instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appenderCfg;
            appender = FileAppender.newBuilder()
                    .setConfiguration(config)
                    .withName(appenderCfg.getName().replace(abyLogConfig.getAppenderNameKey(), abyLogConfig.getName()))
                    .withFileName(fileAppender.getFileName().replaceAll(abyLogConfig.getFileNameKey(), abyLogConfig.getFileName()))
                    .withLayout(fileAppender.getLayout())
                    .withFilter(fileAppender.getFilter())
                    .build();
        } else {
            LogManager.getLogger(LogFactory.class).warn("unsupported appender type ,appender type not in(RollingFileAppender,ConsoleAppender,FileAppender)", appenderCfg.getClass());
        }
        if (appender != null) {
            if (!appender.isStarted()) {
                appender.start();
            }
            config.addAppender(appender);
        }
        return appender;
    }


    public void start(Logger cfgLogger, String clKey) {
        //为false时，返回多个LoggerContext对象， true：返回唯一的单例LoggerContext
        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        List<AppenderRef> appenderRefs = new ArrayList<>();
        Map<String, Appender> appenderMap = ((org.apache.logging.log4j.core.Logger) cfgLogger).getAppenders();
        List<Appender> appenders = new ArrayList<>();
        appenderMap.forEach((key, appenderCfg) -> {
            Appender appender;
            if (appenderCfg instanceof ConsoleAppender) {
                appender = appenderCfg;
            } else {
                appender = getAppender(clKey, config, appenderCfg);
            }
            if (appender != null && appender.isStopped()) {
                appender.start();
            }
            if (appender != null) {
                AppenderRef ref = AppenderRef.createAppenderRef(appender.getName(), Level.ALL, null);
                appenderRefs.add(ref);
                appenders.add(appender);
            }
        });
        if (CollectionUtils.isEmpty(appenders)) {
            return;
        }
        AppenderRef[] refs = new AppenderRef[appenderRefs.size()];
        refs = appenderRefs.toArray(refs);
        LoggerConfig loggerConfig = LoggerConfig.createLogger(false, cfgLogger.getLevel(), clKey, "true", refs, null, config, null);
        appenders.stream().forEach(e ->
                loggerConfig.addAppender(e, Level.ALL, null)
        );
        config.addLogger(clKey, loggerConfig);
        ctx.updateLoggers(config);
    }


    private Appender getAppender(String clKey, Configuration config, Appender appenderCfg) {
        Appender appender = null;
        if (appenderCfg instanceof RollingFileAppender) {
            RollingFileAppender rollingFileAppender = (RollingFileAppender) appenderCfg;
            appender = RollingFileAppender.newBuilder()
                    .setConfiguration(config)
                    .withName(clKey)
                    .withFileName(rollingFileAppender.getFileName())
                    .withFilePattern(rollingFileAppender.getFilePattern())
                    .withLayout(rollingFileAppender.getLayout())
                    .withFilter(rollingFileAppender.getFilter())
                    .withPolicy(rollingFileAppender.getTriggeringPolicy())
                    .withStrategy(rollingFileAppender.getManager().getRolloverStrategy())
                    .build();
        } else if (appenderCfg instanceof FileAppender) {
            FileAppender fileAppender = (FileAppender) appenderCfg;
            appender = FileAppender.newBuilder()
                    .setConfiguration(config)
                    .withName(appenderCfg.getName())
                    .withFileName(fileAppender.getFileName())
                    .withLayout(fileAppender.getLayout())
                    .withFilter(fileAppender.getFilter())
                    .build();
        } else {
            LogManager.getLogger(LogFactory.class).warn("unsupported appender type ,appender type not in(RollingFileAppender,ConsoleAppender,FileAppender)", appenderCfg.getClass());
        }
        if (appender != null) {
            config.addAppender(appender);
        }
        return appender;
    }


    private static Map<String, Logger> loggerMap = new HashMap<>();

    /**
     * 获取Logger
     *
     * @param abyLogConfig
     * @return
     */
    public Logger createLogger(LogConfig abyLogConfig) {
        Logger logger = loggerMap.get(abyLogConfig.getName());
        if (logger != null) {
            return logger;
        }
        lock.lock();
        try {
            if (!LogManager.exists(abyLogConfig.getName())) {
                start(abyLogConfig);
                loggerMap.put(abyLogConfig.getName(), LogManager.getLogger(abyLogConfig.getName()));
            }
        } finally {
            lock.unlock();
        }
        return loggerMap.get(abyLogConfig.getName());
    }

    /**
     * 获取Logger
     *
     * @param logger
     * @param clazz
     * @return
     */
    public Logger createLogger(Logger logger, Class clazz) {
        String clKey = String.join("#", logger.getName(), clazz.getName());
        Logger rl = loggerMap.get(clKey);
        if (rl != null) {
            return rl;
        }
        lock.lock();
        try {
            if (!LogManager.exists(clKey)) {
                start(logger, clKey);
                loggerMap.put(clKey, LogManager.getLogger(clKey));
            }
        } finally {
            lock.unlock();
        }
        return loggerMap.get(clKey);
    }

}