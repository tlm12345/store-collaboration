package com.tlm.storecollab.model.vo.space.analyse;

import lombok.Data;

@Data
public class SpaceCategoryAnalyseVO {
    /**
     * 类别名称
     */
    private String category;

    /**
     * 类别占用空间大小
     */
    private Long spaceSizeUsed;

    /**
     * 类别图片数量
     */
    private Long spaceTotalCount;
}
