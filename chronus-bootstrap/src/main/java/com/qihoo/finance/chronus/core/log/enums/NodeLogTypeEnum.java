package com.qihoo.finance.chronus.core.log.enums;

/**
 * Created by xiongpu on 2019/11/2.
 */
public enum NodeLogTypeEnum {
    START_REGISTER("START_REGISTER", "启动注册"),
    NODE_OFFLINE("NODE_OFFLINE", "节点下线"),
    NODE_ONLINE("NODE_ONLINE", "节点上线"),
    MASTER_TASK_ASSIGN("MASTER_TASK_ASSIGN", "Master-Task分配"),
    MASTER_ELECTION("MASTER_ELECTION", "Master-选举"),
    MASTER_ELECTED("MASTER_ELECTED", "Master-当选"),
    WORKER_LOAD_TASK("WORKER_LOAD_TASK", "Worker-加载任务"),
    ;

    private String code;
    private String desc;

    NodeLogTypeEnum(String code, String desc) {
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
