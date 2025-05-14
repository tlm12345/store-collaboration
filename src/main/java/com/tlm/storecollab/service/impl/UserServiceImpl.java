package com.tlm.storecollab.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.mapper.UserMapper;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.request.UserLoginRequest;
import com.tlm.storecollab.model.request.UserRegisterRequest;
import com.tlm.storecollab.service.UserService;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.common.ErrorCode; // 添加错误码导入
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public long register(UserRegisterRequest request) {
        // 校验账户是否为空
        String userAccount = request.getUserAccount();
        ThrowUtils.throwIf(userAccount == null || userAccount.isEmpty(),
                           ErrorCode.PARAMS_ERROR, "用户账户不能为空");
        // 校验账户长度
        ThrowUtils.throwIf(userAccount.length() < 5 || userAccount.length() > 20,
                           ErrorCode.PARAMS_ERROR, "用户账户长度必须在5-20之间");

        // 校验密码是否为空
        String userPassword = request.getUserPassword();
        ThrowUtils.throwIf(userPassword == null || userPassword.isEmpty(),
                           ErrorCode.PARAMS_ERROR, "用户密码不能为空");
        // 校验密码长度
        ThrowUtils.throwIf(userPassword.length() < 6 || userPassword.length() > 10,
                           ErrorCode.PARAMS_ERROR, "用户密码长度必须在6-10之间");

        // 校验确认密码是否为空
        String confirmPassword = request.getConfirmPassword();
        ThrowUtils.throwIf(confirmPassword == null || confirmPassword.isEmpty(),
                           ErrorCode.PARAMS_ERROR, "确认密码不能为空");
        // 校验确认密码是否与密码一致
        ThrowUtils.throwIf(!userPassword.equals(confirmPassword),
                           ErrorCode.PARAMS_ERROR, "确认密码与密码不一致");

        // 校验账户是否已存在
        User queryConditionUser = new User();
        queryConditionUser.setUserAccount(userAccount);
        User resUser = queryUserWithCondition(queryConditionUser);
        ThrowUtils.throwIf(ObjectUtil.isNotEmpty(resUser), ErrorCode.PARAMS_ERROR, "用户账户已存在");

        // 创建用户对象并插入数据库
        User newUser = new User();
        newUser.setUserAccount(userAccount);
        String encryptedPassword = this.encryptPassword(userPassword, UserConstant.SALT);
        newUser.setUserPassword(encryptedPassword);
        // 使用 MyBatis-Plus 的 save 方法插入数据
        boolean saved = this.save(newUser);
        ThrowUtils.throwIf(!saved, ErrorCode.SYSTEM_ERROR, "用户注册失败");
        return newUser.getId();
    }

    @Override
    public User login(UserLoginRequest request) {
        // 校验账户是否为空
        String userAccount = request.getUserAccount();
        ThrowUtils.throwIf(StrUtil.isBlank(userAccount), ErrorCode.PARAMS_ERROR, "用户账户不能为空");

        // 校验密码是否为空
        String userPassword = request.getUserPassword();
        ThrowUtils.throwIf(StrUtil.isBlank(userPassword), ErrorCode.PARAMS_ERROR, "用户密码不能为空");

        // 查询用户是否存在
        User queryConditionUser = new User();
        queryConditionUser.setUserAccount(userAccount);
        User resUser = queryUserWithCondition(queryConditionUser);
        ThrowUtils.throwIf(ObjectUtil.isEmpty(resUser), ErrorCode.PARAMS_ERROR, "用户不存在");

        // 校验密码是否正确
        String encryptedPassword = this.encryptPassword(userPassword, UserConstant.SALT);
        ThrowUtils.throwIf(!encryptedPassword.equals(resUser.getUserPassword()), ErrorCode.PARAMS_ERROR, "用户密码错误");

        return resUser;
    }

    @Override
    public QueryWrapper<User> getQueryWrapper(User user) {
        Long id = user.getId();
        String userAccount = user.getUserAccount();
        String userName = user.getUserName();
        String userProfile = user.getUserProfile();
        String userRole = user.getUserRole();

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(id != null, "id", id);
        queryWrapper.eq(!StrUtil.isBlank(userAccount), "userAccount", userAccount);
        queryWrapper.like(!StrUtil.isBlank(userName), "userName", userName);
        queryWrapper.like(!StrUtil.isBlank(userProfile), "userProfile", userProfile);
        queryWrapper.eq(!StrUtil.isBlank(userRole), "userRole", userRole);

        return queryWrapper;
    }

    @Override
    public String encryptPassword(String password, String salt) {
        return SecureUtil.md5(password + salt);
    }

    private User queryUserWithCondition(User user) {
        QueryWrapper<User> queryWrapper = getQueryWrapper(user);
        return this.getOne(queryWrapper);
    }

}