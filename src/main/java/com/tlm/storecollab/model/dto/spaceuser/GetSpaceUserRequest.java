package com.tlm.storecollab.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetSpaceUserRequest implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;
    /**
     * 用户id
     */
    private Long userId;
}
