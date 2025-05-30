package com.tlm.storecollab.api.aliyunapi.model;

import lombok.Getter;

/**
 * 任务状态枚举。
 */
@Getter
public enum TaskStatusEnum {
    PENDING("任务排队中", "PENDING"),
    RUNNING("任务处理中", "RUNNING"),
    SUCCEEDED("任务执行成功", "SUCCEEDED"),
    FAILED("任务执行失败", "FAILED"),
    CANCELED("任务取消成功", "CANCELED"),
    UNKNOWN("任务不存在或状态未知", "UNKNOWN");

    private final String description;

    private final String value;

    TaskStatusEnum(String description, String value) {
        this.description = description;
        this.value = value;
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
