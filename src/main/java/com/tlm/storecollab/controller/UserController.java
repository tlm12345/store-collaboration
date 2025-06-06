package com.tlm.storecollab.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.manager.auth.StpKit;
import com.tlm.storecollab.model.dto.user.*;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.UserService;
import com.tlm.storecollab.model.vo.LoginUserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;


@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private  UserService userService;


    /**
     * 用户注册
     * @param request
     * @return
     */
    @PostMapping("/register")
    public BaseResponse<Long> register(@RequestBody UserRegisterRequest request) {

        ThrowUtils.throwIf(ObjectUtil.isEmpty(request), ErrorCode.NULL_ERROR);

        // 调用 UserService 进行注册
        long userId = userService.register(request);
        return ResultUtils.success(userId);
    }

    @PostMapping("/login")
    public BaseResponse<LoginUserVO> login(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request) {
        // 校验请求参数是否为空
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userLoginRequest), ErrorCode.PARAMS_ERROR, "登录请求参数不能为空");

        // 调用 UserService 进行登录
        User loginUser = userService.login(userLoginRequest);

        // 将用户信息存入 session
        HttpSession session = request.getSession();
        session.setAttribute(UserConstant.USER_LOGIN_STATE, loginUser);
        // 使用sa-token进行登录态管理
        StpKit.SPACE.login(loginUser.getId());
        StpKit.SPACE.getSession().set(UserConstant.USER_LOGIN_STATE, loginUser);

        return ResultUtils.success(userService.getLoginUserVO(loginUser));
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

    /**
     * 用户登出
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> logout(HttpServletRequest request){
        userService.logout(request);

        // 使用sa-token释放用户登录态
        StpKit.SPACE.logout();
        return ResultUtils.success(true);
    }

    @PostMapping("/add")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Long> addUser(@RequestBody UserAddRequest userAddRequest) {

        ThrowUtils.throwIf(ObjectUtil.isEmpty(userAddRequest), ErrorCode.NULL_ERROR);

        // 调用 UserService 进行注册
        User user = new User();
        BeanUtil.copyProperties(userAddRequest, user);
        boolean save = userService.save(user);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "用户注册失败");
        return ResultUtils.success(user.getId());
    }

    @PostMapping("/delete")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> deleteUser(DeleteRequest deleteRequest){
        ThrowUtils.throwIf(ObjectUtil.isEmpty(deleteRequest), ErrorCode.NULL_ERROR);
        Long id = deleteRequest.getId();
        ThrowUtils.throwIf(ObjectUtil.isNull(id), ErrorCode.PARAMS_ERROR);
        boolean b = userService.removeById(id);
        return ResultUtils.success(b);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Boolean> updateUser(@RequestBody UserUpdateRequest userUpdateRequest){
        ThrowUtils.throwIf(ObjectUtil.isEmpty(userUpdateRequest), ErrorCode.PARAMS_ERROR);

        Boolean aBoolean = userService.updateUser(userUpdateRequest);
        return ResultUtils.success(aBoolean);
    }

    @GetMapping("/get")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<UserVO> getUser(@RequestParam("id") Long id){
        User user = userService.getById(id);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(user), ErrorCode.PARAMS_ERROR);

        return ResultUtils.success(userService.getUserVO(user));
    }

    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = "admin")
    public BaseResponse<Page<UserVO>> getUserList(@RequestBody UserQueryRequest userQueryRequest){
        ThrowUtils.throwIf(userQueryRequest == null, ErrorCode.PARAMS_ERROR);

        long current = userQueryRequest.getPageNum();
        long pageSize = userQueryRequest.getPageSize();

        QueryWrapper<User> queryWrapper = userService.getQueryWrapper(userQueryRequest);
        Page<User> page = userService.page(new Page<>(current, pageSize), queryWrapper);
        Page<UserVO> userVOPage = new Page<>(current, pageSize, page.getTotal());
        List<UserVO> userVOList = userService.getUserVOList(page.getRecords());
        userVOPage.setRecords(userVOList);

        return ResultUtils.success(userVOPage);
    }
}