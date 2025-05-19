package com.tlm.storecollab.model.dto.picture;


import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class PictureUpdateRequest implements Serializable {

    /**
     * 图片 id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

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

    /**
     * 当前审核状态
     */
    private Integer viewStatus;

    /**
     * 审核理由（原因）
     */
    private String viewMessage;
}
