package com.tlm.storecollab.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.Query;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.dto.picture.*;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceLevelEnum;
import com.tlm.storecollab.model.vo.PictureVO;
import com.tlm.storecollab.model.vo.SpaceVO;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 空间接口
 */
@Slf4j
@RestController
@RequestMapping("/space")
public class SpaceController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;


    @PostMapping("/create")
    public BaseResponse<Long> createPrivateSpace(@RequestBody SpaceCreateRequest spaceCreateRequest, HttpServletRequest request){
        ThrowUtils.throwIf(spaceCreateRequest == null, ErrorCode.NULL_ERROR);
        User loginUser = userService.getLoginUser(request);
        Long spaceId = spaceService.createSpace(spaceCreateRequest, loginUser);
        return ResultUtils.success(spaceId);
    }


    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpace(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (ObjectUtil.isNull(deleteRequest) || ObjectUtil.isNull(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        // 查找要删除的空间
        Long spaceId = deleteRequest.getId();
        Space space = spaceService.getById(spaceId);
        ThrowUtils.throwIf(space == null, ErrorCode.NULL_ERROR);
        // 校验要删除的空间，是否是当前用户的空间
        User loginUser = userService.getLoginUser(request);
        Long userId = loginUser.getId();
        ThrowUtils.throwIf(!ObjUtil.equals(userId, space.getUserId()), ErrorCode.NO_AUTH);

        // TODO: 后面可以修改为批量删除以提高性能
        // 迭代删除用户私人空间下的图片
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        pictureQueryWrapper.eq("userId", userId);
        pictureQueryWrapper.eq("spaceId", spaceId);
        List<Picture> privatePicList = pictureService.list(pictureQueryWrapper);
        for (Picture p : privatePicList) {
            pictureService.removePrivatePictureAndReleaseSpace(userId, p.getId());
        }

        // 删除用户私有空间记录
        boolean b = spaceService.removeById(spaceId);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateSpace(@RequestBody SpaceUpdateRequest spaceUpdateRequest){
        ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        spaceService.validUpdateSpaceRequest(spaceUpdateRequest);

        Long spaceId = spaceUpdateRequest.getId();
        String spaceName = spaceUpdateRequest.getSpaceName();
        Integer spaceLevel = spaceUpdateRequest.getSpaceLevel();

        Space oldSpace = spaceService.getById(spaceId);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.PARAMS_ERROR);

        Space newSpace = new Space();
        newSpace.setId(spaceId);
        if (StrUtil.isNotBlank(spaceName)){
            newSpace.setSpaceName(spaceUpdateRequest.getSpaceName());
        }
        if (spaceLevel != null){
            SpaceLevelEnum enumByValue = SpaceLevelEnum.getEnumByValue(spaceLevel);
            ThrowUtils.throwIf(enumByValue == null, ErrorCode.PARAMS_ERROR, "空间等级不存在");

            Long maxCount = enumByValue.getMaxCount();
            Long maxSize = enumByValue.getMaxSize();
            Integer value = enumByValue.getValue();
            newSpace.setSpaceMaxSize(maxSize);
            newSpace.setSpaceMaxCount(maxCount);
            newSpace.setSpaceLevel(value);
        }
        boolean b = spaceService.updateById(newSpace);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody SpaceUpdateRequest spaceUpdateRequest,
                                             HttpServletRequest request){
        ThrowUtils.throwIf(spaceUpdateRequest == null, ErrorCode.PARAMS_ERROR);
        spaceService.validUpdateSpaceRequest(spaceUpdateRequest);

        Long spaceId = spaceUpdateRequest.getId();
        Space oldSpace = spaceService.getById(spaceId);
        ThrowUtils.throwIf(oldSpace == null, ErrorCode.PARAMS_ERROR);

        // 暂时只允许修改空间名称
        Space newSpace = new Space();
        newSpace.setId(spaceId);
        newSpace.setSpaceName(spaceUpdateRequest.getSpaceName());
        newSpace.setEditTime(new Date());
        boolean b = spaceService.updateById(newSpace);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<SpaceVO> getSpaceById(@RequestParam("id") Long id, HttpServletRequest request){
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);

        Space space = spaceService.getById(id);
        Long userId = space.getUserId();
        User user = userService.getById(userId);

        SpaceVO spaceVO = SpaceVO.objToVO(space);
        spaceVO.setUserVO(UserVO.objToVO(user));

        return ResultUtils.success(spaceVO);
    }
}