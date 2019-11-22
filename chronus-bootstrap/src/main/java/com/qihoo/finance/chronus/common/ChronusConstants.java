package com.qihoo.finance.chronus.common;

/**
 * Created by xiongpu on 2018/5/18.
 */
public class ChronusConstants {
    public static final String Y = "Y";
    public static final String N = "N";
    public static final String DEF_TAG = "default";
    public static final String DEF_CLUSTER = "default";
    public static final String API_TOKEN_KEY = "x-auth-token";
    public static final String REGISTER_TIME = "registerTime";
    public static final String CLUSTER = "cluster";
    public static final String DATA_VERSION = "dataVersion";
    public static final String IS_MASTER = "isMaster";
    public static final String ENABLE_MASTER = "enableMaster";
    public static final String ENABLE_EXECUTOR = "enableExecutor";
    public static final String STATE = "state";
    public static final String ADDRESS = "address";
    public static final String TAG = "tag";
    public static final String HOST_NAME = "hostName";
    public static final String NODE_NAME_CHRONUS = "chronus";

    public static String STS_PAUSE = "pause";
    public static String STS_NORMAL = "normal";

    public static final String CFG_LOG_NAME = "TaskCfgLogger";
    public static final String CFG_MONITOR_LOG_NAME = "TaskCfgMonitorLogger";
    public static final String LOG_FILE_NAME_KEY = "task-log-factory";
    public static final String APPENDER_NAME_KEY = "TaskCfg";
    public static final String MONITOR_LOGGER_PATTERN_EXECUTE = "{}|{}-{}|{}|{}|execute|{}|{}ms";
    public static final String MONITOR_LOGGER_PATTERN_SELECT_TASKS = "{}|{}-{}|{}|{}|selectTasks|{}|{}|{}ms";
    public static final String DATE_FORMAT_YYYYMMDDHHMMSS = "yyyy-MM-dd HH:mm:ss";

    public static final int MILLISECOND_2_SECOND=1000;
}
