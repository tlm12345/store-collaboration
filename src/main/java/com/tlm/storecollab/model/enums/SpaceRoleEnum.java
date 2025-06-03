package com.tlm.storecollab.model.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

@Getter
public enum SpaceRoleEnum {

    ADMIN("管理员", "admin"),
    VIEWER("浏览者", "viewer"),
    EDITOR("编辑者", "editor");

    private final String text;
    private final String value;

    SpaceRoleEnum(String text, String value){
        this.text = text;
        this.value = value;
    }

    public static SpaceRoleEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) return null;

        for (SpaceRoleEnum spaceRoleEnum : SpaceRoleEnum.values()) {
            if (spaceRoleEnum.value.equals(value)) {
                return spaceRoleEnum;
            }
        }
        return null;
    }
}
