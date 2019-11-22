package com.qihoo.finance.chronus.core.log;

import com.qihoo.finance.chronus.core.log.pojo.LogConfig;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

import static com.qihoo.finance.chronus.common.ChronusConstants.*;

/**
 * Created by xiongpu on 2017/7/3.
 */
public class LogUtil {
    private static Map<String, LogConfig> logConfigMap = new HashMap<>();
    private static Map<String, LogConfig> monitorLogConfigMap = new HashMap<>();

    public static Logger getLogger(String sysCode) {
        return LogFactory.getLogFactory().createLogger(LogUtil.buildLogConfig(sysCode));
    }

    public static Logger getLogger(String sysCode, Class clazz) {
        Logger sysLogger = LogFactory.getLogFactory().createLogger(LogUtil.buildLogConfig(sysCode));
        return LogFactory.getLogFactory().createLogger(sysLogger, clazz);
    }

    public static Logger getMonitorLogger(String sysCode) {
        return LogFactory.getLogFactory().createLogger(LogUtil.buildMonitorLogConfig(sysCode));
    }


    private static LogConfig buildLogConfig(String sysCode) {
        LogConfig logConfig = logConfigMap.computeIfAbsent(sysCode, k -> new LogConfig().setCfgLogName(CFG_LOG_NAME).setName(sysCode).setFileName(sysCode).setFileNameKey(LOG_FILE_NAME_KEY).setAppenderNameKey(APPENDER_NAME_KEY));
        return logConfig;
    }

    private static LogConfig buildMonitorLogConfig(String sysCode) {
        LogConfig logConfig = monitorLogConfigMap.computeIfAbsent(sysCode, k -> new LogConfig().setCfgLogName(CFG_MONITOR_LOG_NAME).setName("Monitor." + sysCode).setFileName(sysCode).setFileNameKey(LOG_FILE_NAME_KEY).setAppenderNameKey(APPENDER_NAME_KEY));
        return logConfig;
    }


}
