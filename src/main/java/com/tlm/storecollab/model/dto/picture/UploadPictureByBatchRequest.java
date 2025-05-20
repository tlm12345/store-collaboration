package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

@Data
public class UploadPictureByBatchRequest {

    /**
     * 查询关键字
     */
    private String q;

    /**
     * 前缀名称
     */
    private String prefixName;

    /**
     * 批量大小
     */
    private Integer count = 10;
}
