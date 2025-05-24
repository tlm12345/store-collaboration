package com.tlm.storecollab.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceLevelEnum;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.mapper.SpaceMapper;
import com.tlm.storecollab.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
* @author 12483
* @description 针对表【space(空间)】的数据库操作Service实现
* @createDate 2025-05-24 22:22:35
*/
@Service
public class SpaceServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceService{

    @Resource
    private UserService userService;


    @Override
    public Long createPrivateSpace(SpaceCreateRequest spaceCreateRequest, User loginUser) {
//        1. 校验请求参数
        ThrowUtils.throwIf(spaceCreateRequest == null, ErrorCode.PARAMS_ERROR);
//        2. 判断用户是否创建过空间
        Long userId = loginUser.getId();
        QueryWrapper<Space> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId", userId);
        boolean exists = this.exists(objectQueryWrapper);
//                * 是， 拒绝请求
        ThrowUtils.throwIf(exists, ErrorCode.PRIVATE_SPACE_HAS_EXISTS);
//        3. 校验用户权限，如果是普通用户，则只能创建普通空间
        Space space = new Space();
        Boolean isAdmin = userService.isAdmin(loginUser);
        if (!isAdmin){
            SpaceLevelEnum common = SpaceLevelEnum.COMMON;
            Integer level = common.getValue();
            Long maxCount = common.getMaxCount();
            Long maxSize = common.getMaxSize();
            space.setSpaceLevel(level);
            space.setSpaceMaxCount(maxCount);
            space.setSpaceMaxSize(maxSize);
        }

        SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceCreateRequest.getSpaceLevel());
        ThrowUtils.throwIf(enumByValue == null, ErrorCode.PARAMS_ERROR);

        Integer level = enumByValue.getValue();
        Long maxCount = enumByValue.getMaxCount();
        Long maxSize = enumByValue.getMaxSize();
        space.setSpaceLevel(level);
        space.setSpaceMaxCount(maxCount);
        space.setSpaceMaxSize(maxSize);

        space.setSpaceName(spaceCreateRequest.getSpaceName());
        space.setUserId(userId);
//        4. 设置好新建空间的相关属性，插入到数据库中
        boolean save = this.save(space);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
//        5. 返回成功创建响应
        return space.getId();
    }
}




