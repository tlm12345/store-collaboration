package com.tlm.storecollab.model.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class SpaceUserVO implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 空间 id
     */
    private Long spaceId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间角色 admin-管理员;viewer-浏览者;editor-编辑者
     */
    private String spaceRole;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 团队空间信息
     */
    private SpaceVO spaceVO;

    /**
     * 创建空间团队的用户信息
     */
    private UserVO userVO;
}
