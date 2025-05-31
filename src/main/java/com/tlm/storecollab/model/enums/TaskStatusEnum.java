package com.tlm.storecollab.model.enums;

import lombok.Getter;

@Getter
public enum TaskStatusEnum {


    PENDING("PENDING"),
    RUNNING("RUNNING"),
    SUCCEEDED("SUCCEEDED"),
    FAILED("FAILED"),
    CANCELED("CANCELED"),
    UNKNOWN("UNKNOWN");


    /**
     * 状态值
     */
    public final String value;

    TaskStatusEnum(String status){
        this.value = status;
    }

     public static TaskStatusEnum getEnumByValue(String value) {
        for (TaskStatusEnum status : values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return null;
    }

}
