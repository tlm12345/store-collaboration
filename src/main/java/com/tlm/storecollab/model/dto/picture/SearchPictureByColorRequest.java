package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

/**
 * 根据颜色搜索图片请求
 * author tlm
 */
@Data
public class SearchPictureByColorRequest implements Serializable {

    /**
     * 色调编码
     */
    private String picAve;

    /**
     * 空间Id
     */
    private Long spaceId;
}
