package com.tlm.storecollab.service.impl;

import cn.hutool.core.util.ObjUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceLevelEnum;
import com.tlm.storecollab.service.PictureService;
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

        return (spaceSizeUsed < spaceMaxSize) || (spaceTotalCount < spaceMaxCount);
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
}




