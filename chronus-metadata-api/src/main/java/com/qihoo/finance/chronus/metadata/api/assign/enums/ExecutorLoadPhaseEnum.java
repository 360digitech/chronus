package com.qihoo.finance.chronus.metadata.api.assign.enums;

/**
 * Created by xiongpu on 2019/9/7.
 */
public enum ExecutorLoadPhaseEnum {

    RESET(-1, "需重新加载"),
    INIT(0, "初始化"),
    REMOVE(1, "变更调度需移除"),
    ADD(2, "变更调度需补充"),
    FINISH(3, "处理完成"),
    OFFLINE(-9, "节点下线"),
    ;

    private Integer phase;

    private String desc;

    ExecutorLoadPhaseEnum(Integer phase, String desc) {
        this.phase = phase;
        this.desc = desc;
    }

    public Integer getPhase() {
        return phase;
    }

    public String getDesc() {
        return desc;
    }

    public static boolean isResetPhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.RESET.getPhase().intValue() == phase.intValue();
    }

    public static boolean isRemovePhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.REMOVE.getPhase().intValue() == phase.intValue();
    }

    public static boolean isInitPhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.INIT.getPhase().intValue() == phase.intValue();
    }

    public static boolean isAddPhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.ADD.getPhase().intValue() == phase.intValue();
    }

    public static boolean isFinishPhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.FINISH.getPhase().intValue() == phase.intValue();
    }

    public static boolean isOfflinePhase(Integer phase) {
        return phase != null && ExecutorLoadPhaseEnum.OFFLINE.getPhase().intValue() == phase.intValue();
    }

    public boolean isEquals(Integer phase) {
        return phase != null && this.getPhase().intValue() == phase.intValue();
    }
}
