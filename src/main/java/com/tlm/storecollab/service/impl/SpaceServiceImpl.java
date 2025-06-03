package com.tlm.storecollab.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.dto.spaceuser.AddSpaceUserRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceLevelEnum;
import com.tlm.storecollab.model.enums.SpaceRoleEnum;
import com.tlm.storecollab.model.enums.SpaceTypeEnum;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.mapper.SpaceMapper;
import com.tlm.storecollab.service.SpaceUserService;
import com.tlm.storecollab.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Resource
    private SpaceUserService spaceUserService;



    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createSpace(SpaceCreateRequest spaceCreateRequest, User loginUser) {
//        1. 校验请求参数
        ThrowUtils.throwIf(spaceCreateRequest == null, ErrorCode.PARAMS_ERROR);
//        2. 判断用户是否创建过空间::私人空间和团队空间都只能各创建一个
        Long userId = loginUser.getId();
        QueryWrapper<Space> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("userId", userId);
        Integer spaceType = spaceCreateRequest.getSpaceType();
        SpaceTypeEnum enumByValue1 = SpaceTypeEnum.getEnumByValue(spaceType);
        ThrowUtils.throwIf(enumByValue1 == null, ErrorCode.PARAMS_ERROR, "不支持的空间类型");
        objectQueryWrapper.eq("spaceType", enumByValue1.getValue());
        boolean exists = this.exists(objectQueryWrapper);
//                * 是， 拒绝请求
        ThrowUtils.throwIf(exists, ErrorCode.PRIVATE_SPACE_HAS_EXISTS);
//        3. 校验用户权限，如果是普通用户，则只能创建普通空间
        Space space = new Space();
        Boolean isAdmin = userService.isAdmin(loginUser);
        Integer level = null;
        Long maxCount = null;
        Long maxSize = null;
        if (!isAdmin){
            SpaceLevelEnum common = SpaceLevelEnum.COMMON;
            level = common.getValue();
            maxCount = common.getMaxCount();
            maxSize = common.getMaxSize();
        }else {
            SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceCreateRequest.getSpaceLevel());
            ThrowUtils.throwIf(enumByValue == null, ErrorCode.PARAMS_ERROR);
            level = enumByValue.getValue();
            maxCount = enumByValue.getMaxCount();
            maxSize = enumByValue.getMaxSize();
        }

        space.setSpaceLevel(level);
        space.setSpaceMaxCount(maxCount);
        space.setSpaceMaxSize(maxSize);

        space.setSpaceName(spaceCreateRequest.getSpaceName());
        space.setUserId(userId);
        space.setSpaceType(spaceCreateRequest.getSpaceType());
//        4. 设置好新建空间的相关属性，插入到数据库中
        boolean save = this.save(space);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR);
        // 将创建团队空间的用户加入到该团队空间的成员表中
        if (SpaceTypeEnum.Team.getValue().equals(spaceCreateRequest.getSpaceType())){
            AddSpaceUserRequest addSpaceUserRequest = new AddSpaceUserRequest(space.getId(), loginUser.getId(), SpaceRoleEnum.ADMIN.getValue());
            spaceUserService.addSpaceUser(addSpaceUserRequest, loginUser);
        }
//        5. 返回成功创建响应
        return space.getId();
    }

    @Override
    public boolean validPrivateSpaceIsFree(Long userId) {
        Space space = this.lambdaQuery()
                .eq(Space::getUserId, userId).getEntity();
        return validPrivateSpaceIsFree(space);
    }

    @Override
    public boolean validPrivateSpaceIsFree(Space space) {
        Long spaceMaxCount = space.getSpaceMaxCount();
        Long spaceMaxSize = space.getSpaceMaxSize();
        Long spaceSizeUsed = space.getSpaceSizeUsed();
        Long spaceTotalCount = space.getSpaceTotalCount();

        return (spaceSizeUsed < spaceMaxSize) && (spaceTotalCount < spaceMaxCount);
    }


    public void updateSpaceCapacityInfo(Picture pic, Space space, boolean isAdd) {
        // 获取图片大小
        Long picSize = pic.getPicSize();
        // 更新用户私人空间容量信息
        Long spaceTotalCount = space.getSpaceTotalCount();
        Long spaceSizeUsed = space.getSpaceSizeUsed();

        // 构建一个新的space，只更新发生改变的值
        Space newSpace = new Space();
        newSpace.setId(space.getId());

        if (isAdd){
            newSpace.setSpaceSizeUsed(spaceSizeUsed + picSize);
            newSpace.setSpaceTotalCount(spaceTotalCount + 1);

            space.setSpaceSizeUsed(spaceSizeUsed + picSize);
            space.setSpaceTotalCount(spaceTotalCount + 1);
        }else {
            newSpace.setSpaceSizeUsed(spaceSizeUsed - picSize);
            newSpace.setSpaceTotalCount(spaceTotalCount - 1);

            space.setSpaceSizeUsed(spaceSizeUsed - picSize);
            space.setSpaceTotalCount(spaceTotalCount - 1);
        }

        boolean b = this.updateById(newSpace);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
    }

    @Override
    public void validUpdateSpaceRequest(SpaceUpdateRequest spaceUpdateRequest) {
        Long id = spaceUpdateRequest.getId();
        String spaceName = spaceUpdateRequest.getSpaceName();

        ThrowUtils.throwIf(spaceName.isEmpty() || spaceName.length() > 30, ErrorCode.PARAMS_ERROR, "空间名称不能为空或者过长");
    }

    @Override
    public void validUserHasPrivateSpaceAuth(Long userId, Space space) {
        // 判断用户id是否与空间的私人id一致，不一致，无权限，抛出异常；一致，直接返回。
        ThrowUtils.throwIf(!ObjUtil.equals(userId, space.getUserId()), ErrorCode.NO_AUTH, "无权限");
    }
}




