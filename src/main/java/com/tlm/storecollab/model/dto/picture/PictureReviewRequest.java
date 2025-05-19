package com.tlm.storecollab.model.dto.picture;

import lombok.Data;

import java.io.Serializable;

@Data
public class PictureReviewRequest implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 当前审核状态
     */
    private Integer viewStatus;

    /**
     * 审核理由（原因）
     */
    private String viewMessage;
}
