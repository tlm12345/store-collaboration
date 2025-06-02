package com.tlm.storecollab.model.dto.space.analyse;

import lombok.Data;

@Data
public class SpaceTopNCapacityUsedMostRequest {

    /**
     * 前n个空间
     */
    private Long topN;
}
