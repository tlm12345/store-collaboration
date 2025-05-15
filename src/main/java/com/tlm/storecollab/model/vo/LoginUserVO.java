package com.tlm.storecollab.model.vo;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

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


    /**
     * 账号
     */
    private String userAccount;



    /**
     * 用户头像
     */
    private String userAvatar;
}