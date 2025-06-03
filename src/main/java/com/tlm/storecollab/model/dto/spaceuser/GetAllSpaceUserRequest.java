package com.tlm.storecollab.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

@Data
public class GetAllSpaceUserRequest implements Serializable {

    /**
     * 空间id
     */
    private Long spaceId;
}
