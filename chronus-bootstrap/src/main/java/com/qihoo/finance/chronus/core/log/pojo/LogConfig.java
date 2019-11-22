package com.qihoo.finance.chronus.core.log.pojo;

/**
 * Created by xiongpu on 2017/7/3.
 */
public class LogConfig {
    /**
     * log4j2.xml 里面定义的logger name 用来作为配置模板,目前支持 RollingFileAppender,ConsoleAppender,FileAppender
     */
    private String cfgLogName;
    /**
     * 定义的logger 名称
     */
    private String name;
    /**
     * fileName="${logFilePath}/task-log-factory.log" 要替换的内容 比如这里task-log-factory替换为任务名称
     */
    private String fileNameKey;
    /**
     * 替换值 如任务名
     */
    private String fileName;

    private String appenderNameKey;

    public LogConfig() {

    }

    public String getCfgLogName() {
        return cfgLogName;
    }

    public LogConfig setCfgLogName(String cfgLogName) {
        this.cfgLogName = cfgLogName;
        return this;
    }

    public String getName() {
        return name;
    }

    public LogConfig setName(String name) {
        this.name = name;
        return this;
    }


    public String getFileNameKey() {
        return fileNameKey;
    }

    public LogConfig setFileNameKey(String fileNameKey) {
        this.fileNameKey = fileNameKey;
        return this;
    }

    public String getFileName() {
        return fileName;
    }

    public LogConfig setFileName(String fileName) {
        this.fileName = fileName;
        return this;
    }

    public String getAppenderNameKey() {
        return appenderNameKey;
    }

    public LogConfig setAppenderNameKey(String appenderNameKey) {
        this.appenderNameKey = appenderNameKey;
        return this;
    }
}
