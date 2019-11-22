package com.qihoo.finance.chronus.core.event.enums;

/**
 * Created by xiongpu on 2019/11/2.
 */
public enum EventEnum {
    START_REGISTER("START_REGISTER", "启动注册"),
    NODE_OFFLINE("NODE_OFFLINE", "节点下线"),
    NODE_ONLINE("NODE_ONLINE", "节点上线"),
    MASTER_TASK_ASSIGN("MASTER_TASK_ASSIGN", "Master-Task分配"),
    MASTER_ELECTION("MASTER_ELECTION", "Master-选举"),
    MASTER_ELECTED("MASTER_ELECTED", "Master-当选"),
    EXECUTOR_LOAD_TASK("EXECUTOR_LOAD_TASK", "Executor-加载任务"),
    EXECUTOR_ADD_ERROR_TASK("EXECUTOR_ADD_ERROR_TASK", "Executor-补充异常任务"),
    EXECUTOR_SUBMIT_STATE("EXECUTOR_SUBMIT_STATE", "Executor-状态提交"),
    ;

    private String code;
    private String desc;


    EventEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
