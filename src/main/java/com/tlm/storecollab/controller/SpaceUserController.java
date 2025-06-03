package com.tlm.storecollab.controller;

import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.dto.spaceuser.AddSpaceUserRequest;
import com.tlm.storecollab.model.dto.spaceuser.QuerySpaceUserRequest;
import com.tlm.storecollab.model.dto.spaceuser.UpdateSpaceUserRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.SpaceUser;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceLevelEnum;
import com.tlm.storecollab.model.vo.SpaceUserVO;
import com.tlm.storecollab.model.vo.SpaceVO;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.SpaceUserService;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

/**
 * 空间成员接口
 */
@Slf4j
@RestController
@RequestMapping("/space_user")
public class SpaceUserController {

    @Resource
    private UserService userService;


    @Resource
    private SpaceUserService spaceUserService;

    @PostMapping("/add")
    public BaseResponse<Long> addSpaceUser(@RequestBody AddSpaceUserRequest addSpaceUserRequest, HttpServletRequest request) {
        if (addSpaceUserRequest == null) {
            throw new BusinessException(ErrorCode.NULL_ERROR);
        }

        User loginUser = userService.getLoginUser(request);
        Long id = spaceUserService.addSpaceUser(addSpaceUserRequest, loginUser);
        return ResultUtils.success(id);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteSpaceUser(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        // 校验参数非空
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.NULL_ERROR);
        User loginUser = userService.getLoginUser(request);
        spaceUserService.deleteSpaceUser(deleteRequest, loginUser);
        return ResultUtils.success(true);
    }

    @PostMapping("/update")
    public BaseResponse<Boolean> updateSpaceUser(@RequestBody UpdateSpaceUserRequest updateSpaceUserRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(updateSpaceUserRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        return ResultUtils.success(spaceUserService.updateSpaceUser(updateSpaceUserRequest, loginUser));
    }

    @GetMapping("/get")
    public BaseResponse<SpaceUserVO> getSpaceUser(@RequestParam("spaceId") Long spaceId, @RequestParam("userId") Long userId){
        // 校验参数非空
        ThrowUtils.throwIf(spaceId == null || userId == null, ErrorCode.NULL_ERROR);
        SpaceUser spaceUser = spaceUserService.getSpaceUser(spaceId, userId);
        SpaceUserVO spaceUserVO = spaceUserService.objToVo(spaceUser);
        return ResultUtils.success(spaceUserVO);
    }

    /**
     * 获取团队空间中所有成员
     */
    @GetMapping("/list/my")
    public BaseResponse<List<SpaceUser>> listSpaceUser(@RequestParam("spaceId") Long spaceId){
        // 校验参数非空
        ThrowUtils.throwIf(spaceId == null, ErrorCode.NULL_ERROR);
        List<SpaceUser> spaceUserList = spaceUserService.getAllSpaceUser(spaceId);
        return ResultUtils.success(spaceUserList);
    }

    @PostMapping("/query")
    public BaseResponse<List<SpaceUser>> querySpaceUser(@RequestBody QuerySpaceUserRequest querySpaceUserRequest, HttpServletRequest request){
        // 校验参数
        ThrowUtils.throwIf(querySpaceUserRequest == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<SpaceUser> spaceUserList = spaceUserService.querySpaceUserWithCondition(querySpaceUserRequest);
        return ResultUtils.success(spaceUserList);
    }
}