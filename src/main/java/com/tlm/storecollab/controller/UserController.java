package com.tlm.storecollab.controller;

import cn.hutool.core.util.ObjectUtil;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ResultUtils;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.request.UserLoginRequest;
import com.tlm.storecollab.model.request.UserRegisterRequest;
import com.tlm.storecollab.common.BaseResponse;
import com.tlm.storecollab.service.UserService;
import com.tlm.storecollab.model.vo.LoginUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private  UserService userService;


    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest request) {

        ThrowUtils.throwIf(ObjectUtil.isEmpty(request), ErrorCode.NULL_ERROR);

        // 调用 UserService 进行注册
        long userId = userService.register(request);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<User> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验请求参数是否为空
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userLoginRequest), ErrorCode.PARAMS_ERROR, "登录请求参数不能为空");

        // 调用 UserService 进行登录
        User loginUser = userService.login(userLoginRequest);

        // 将用户信息存入 session
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);

        return ResultUtils.success(loginUser);
    }

    // 新增：获取当前登录用户
    @GetMapping("/current")
    public BaseResponse<LoginUserVO> getCurrentUser(HttpServletRequest request) {
        // 从 Session 中获取用户登录态
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userObj), ErrorCode.NOT_LOGIN, "用户未登录");

        // 获取用户 ID 并查询用户信息
        User currentUser = (User) userObj;
        User user = userService.getById(currentUser.getId());
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.SYSTEM_ERROR, "用户不存在");

        // 将 User 对象复制到 LoginUserVO
        LoginUserVO loginUserVO = new LoginUserVO();
        BeanUtils.copyProperties(user, loginUserVO);

        return ResultUtils.success(loginUserVO);
    }
}