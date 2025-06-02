package com.tlm.storecollab.model.vo.space.analyse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpaceTagAnalyseVO implements Serializable {

    /**
     * 标签名称
     */
    private String tag;

    /**
     * 标签下图片的数量
     */
    private Long amount;
}
