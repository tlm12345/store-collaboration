package com.tlm.storecollab.model.dto.space.analyse;

import lombok.Data;

import java.io.Serializable;

@Data
public class SpaceAnalyseRequest implements Serializable {

    private static final long serialVersionUID = 3302480171037212876L;
    /**
     * 查询全空间(私有+公共)
     */
    private Boolean queryAll;

    /**
     * 查询公共空间
     */
    private Boolean queryPublic;

    /**
     * 查询指定空间
     */
    private Long spaceId;
}
