package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

import java.util.List;

/**
 * 批量修改图片信息请求
 */
@Data
public class PictureEditByBatchRequest {

    /**
     * 图片id列表
     */
    private List<Long> pictureIdList;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签
     */
    private String tags;

    /**
     * 批量命名规则
     */
    private String nameRule;
}
