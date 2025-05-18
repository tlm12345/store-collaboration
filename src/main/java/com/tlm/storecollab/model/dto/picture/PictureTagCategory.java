package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

import java.util.List;

@Data
public class PictureTagCategory {

    /**
     * 标签列表
     */
    List<String> tags;

    /**
     * 分类列表
     */
    List<String> categories;
}
