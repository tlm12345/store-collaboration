package com.tlm.storecollab.model.dto.space.analyse;

import lombok.Data;

@Data
public class SpaceUserUploadBehaviorAnalyseRequest extends SpaceAnalyseRequest{

    /**
     * 分析时间维度  example: day, week, year
     */
    private String timeDimension;

    /**
     * 分析的目标用户
     */
    private Long userId;
}
