package com.tlm.storecollab.model.dto.space;

import lombok.Data;


@Data
public class SpaceCreateRequest {

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间等级
     */
    private Integer spaceLevel;
}
