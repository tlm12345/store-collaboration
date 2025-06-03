package com.tlm.storecollab.model.dto.spaceuser;

import lombok.Data;

import java.io.Serializable;

@Data
public class UpdateSpaceUserRequest implements Serializable {

    /**
     * 用户角色记录id
     */
    private Long id;

    /**
     * 用户角色
     */
    private String spaceRole;

}
