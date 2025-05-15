package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.dto.user.UserQueryRequest;
import com.tlm.storecollab.model.dto.user.UserUpdateRequest;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.dto.user.UserLoginRequest;
import com.tlm.storecollab.model.dto.user.UserRegisterRequest;
import com.tlm.storecollab.model.vo.LoginUserVO;
import com.tlm.storecollab.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);


    /**
     * 加密密码
     * @param password
     * @param salt
     * @return
     */
    String encryptPassword(String password, String salt);

    /**
     * 获取用户登录态
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 获取脱敏用户对象
     */
    LoginUserVO getLoginUserVO(User user);

    /**
     * 获取用户视图(脱敏)
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 获取用户列表视图(脱敏)
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 用户登出
     * @param request
     * @return
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 更新用户
     * @param userUpdateRequest
     * @return
     */
    Boolean updateUser(UserUpdateRequest userUpdateRequest);
}