package com.tlm.storecollab.api.imagesearch.model;

import lombok.Data;

@Data
public class ImageSearchResult {

    /**
     * 缩略图网址
     */
    private String thumbUrl;

    /**
     * 图片来源网址
     */
    private String fromUrl;

    /**
     * 完整图像展示网址
     */
    private String objUrl;
}
