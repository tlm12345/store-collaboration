package com.tlm.storecollab.aop;

import cn.hutool.core.util.ObjectUtil;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.UserRoleEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class AuthInterceptor {

    @Around("@annotation(authCheck)")
    public Object doIntercept(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String role = authCheck.mustRole();
        UserRoleEnum userRole = UserRoleEnum.getValueByValue(role);

        // 如果不需要权限，则直接执行
        if (userRole == null){
            return joinPoint.proceed();
        }

        // 获取当前用户所具有的权限
        RequestAttributes requestAttributes = RequestContextHolder.currentRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
        Object attribute = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        ThrowUtils.throwIf(ObjectUtil.isNull(attribute), ErrorCode.NOT_LOGIN);
        User loginUser = (User) attribute;
        String loginUserRole = loginUser.getUserRole();
        UserRoleEnum loginUserRoleEnum = UserRoleEnum.getValueByValue(loginUserRole);

        // 如果用户没有任何权限，则抛出异常
        ThrowUtils.throwIf(ObjectUtil.isNull(loginUserRoleEnum), ErrorCode.NO_AUTH);

        // 如果权限需要为 管理员 才能执行， 而用户又不是管理员
        if (UserRoleEnum.ADMIN.equals(userRole) && !UserRoleEnum.ADMIN.equals(loginUserRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return joinPoint.proceed();
    }
}
