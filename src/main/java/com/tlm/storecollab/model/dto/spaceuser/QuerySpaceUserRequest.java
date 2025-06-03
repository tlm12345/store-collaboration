package com.tlm.storecollab.model.dto.spaceuser;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class QuerySpaceUserRequest {
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
}
