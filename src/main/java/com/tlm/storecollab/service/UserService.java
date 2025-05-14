package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.request.UserLoginRequest;
import com.tlm.storecollab.model.request.UserRegisterRequest;

public interface UserService extends IService<User> {
    /**
     * 用户注册
     * @param request
     * @return
     */
    long register(UserRegisterRequest request);

    /**
     * 用户登录
     * @param request
     * @return
     */
    User login(UserLoginRequest request);

    /**
     * 构建查询条件
     * @param user
     * @return
     */
    QueryWrapper<User> getQueryWrapper(User user);

    /**
     * 加密密码
     * @param password
     * @param salt
     * @return
     */
    String encryptPassword(String password, String salt);
}