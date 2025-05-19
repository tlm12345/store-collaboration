package com.tlm.storecollab.model.enums;

import lombok.Getter;

@Getter
public enum PictureReviewStatusEnum {

    REVIEWING("审核中", 0),
    PASS("审核通过", 1),
    REJECTED("审核拒绝", 2);

    /**
     * 状态文本
     */
    private final String text;
    /**
     * 状态值
     */
    private final Integer value;

    PictureReviewStatusEnum(String text, Integer value) {
        this.text = text;
        this.value = value;
    }

    public PictureReviewStatusEnum getEnumByValue(Integer value){
        for (PictureReviewStatusEnum statusEnum : PictureReviewStatusEnum.values()) {
            if (statusEnum.getValue().equals(value)) {
                return statusEnum;
            }
        }
        return null;
    }
}
