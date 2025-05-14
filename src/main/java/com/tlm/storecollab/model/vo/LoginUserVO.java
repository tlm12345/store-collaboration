package com.tlm.storecollab.model.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 登录用户视图封装类
 */
@Data
public class LoginUserVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 用户角色
     */
    private String userRole;

    /**
     * 用户简介
     */
    private String userProfile;
}