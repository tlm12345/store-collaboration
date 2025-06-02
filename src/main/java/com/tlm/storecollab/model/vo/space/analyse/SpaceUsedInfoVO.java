package com.tlm.storecollab.model.vo.space.analyse;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceUsedInfoVO implements Serializable {

    private static final long serialVersionUID = -2926011692752849135L;
    /**
     * 空间已使用容量
     */
    private Long spaceSizeUsed;

    /**
     * 空间最大容量
     */
    private Long spaceMaxSize;

    /**
     * 空间容量已使用的比例
     */
    private Double spaceUsedRatio;

    /**
     * 当前空间下已有图片数量
     */
    private Long spaceTotalCount;

    /**
     * 当前空间下最大图片数量
     */
    private Long spaceMaxCount;

    /**
     * 空间资源数量占用比例
     */
    private Double spaceCountRatio;
}
