package com.tlm.storecollab.model.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {

    USER("user", "普通用户"),
    ADMIN("admin", "管理员");


    private final String value;
    private final String text;

    UserRoleEnum(String value, String text){
        this.value = value;
        this.text = text;
    }

    public static UserRoleEnum getValueByValue(String value) {
        for (UserRoleEnum userRoleEnum : UserRoleEnum.values()) {
            if (userRoleEnum.getValue().equals(value)) {
                return userRoleEnum;
            }
        }
        return null;
    }
}
