package com.rayleigh.activiti.Enum;


public enum ActivitiEnum {
    /**
     * 流程变量,下一环节审批人
     */
    NEXTASSIGNEES("assignees"),
    /**
     * 流程变量,审批同意操作
     */
    OK("ok"),
    /**
     * 流程变量,审批拒绝操作
     */
    REJECT("reject"),
    /**
     * 流程变量,审批拒绝并且返回到流程开始阶段
     */
    REJECTTOSTART("rejectToStart"),
    /**
     * 流程状态,正在进行
     */
    ONGOING("ongoing"),
    /**
     * 流程状态,流程结束
     */
    FINISH("finish"),
    /**
     * 流程变量,加签操作
     */
    ADDSIGN("addSign"),
    /**
     * 流程变量,转发操作
     */
    RELAY("relay");

    String value;

    ActivitiEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}

