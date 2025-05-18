package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

@Data
public class PictureEditRequest {
    /**
     * 图片 id
     *
     */
    private Long id;
    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（JSON 数组）
     */
    private String tags;
}
