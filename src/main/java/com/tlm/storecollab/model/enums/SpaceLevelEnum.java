package com.tlm.storecollab.model.enums;

import com.tlm.storecollab.constant.CapacityUnit;
import lombok.Getter;

@Getter
public enum SpaceLevelEnum {

    COMMON("普通版", 0, 100 * CapacityUnit.MB, 100L),
    PROFESSIONAL("专业版", 1, 500 * CapacityUnit.MB, 500L),
    FLAGSHIP("旗舰版", 2, 1000 * CapacityUnit.MB, 1000L);

    /**
     * 文本描述
     */
    private final String text;
    /**
     * 值
     */
    private final Integer value;

    /**
     * 最大容量
     */
    private final Long maxSize;
    /**
     * 最大记录数
     */
    private final Long maxCount;

    SpaceLevelEnum(String text, Integer value, Long maxSize, Long maxCount){
        // 初始化所有参数
        this.text = text;
        this.value = value;
        this.maxSize = maxSize;
        this.maxCount = maxCount;
    }

    public static SpaceLevelEnum getEnumByValue(Integer value){
        for (SpaceLevelEnum spaceLevelEnum : SpaceLevelEnum.values()) {
            if (spaceLevelEnum.getValue().equals(value)) {
                return spaceLevelEnum;
            }
        }
        return null;
    }
}
