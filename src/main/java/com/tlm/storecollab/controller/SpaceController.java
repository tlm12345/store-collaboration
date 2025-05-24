package com.tlm.storecollab.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.dto.picture.*;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.PictureVO;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
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
        Long spaceId = spaceService.createPrivateSpace(spaceCreateRequest, loginUser);
        return ResultUtils.success(spaceId);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (ObjectUtil.isNull(deleteRequest) || ObjectUtil.isNull(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long id = deleteRequest.getId();
        Picture oldPic = pictureService.getById(id);
        ThrowUtils.throwIf(oldPic == null, ErrorCode.NULL_ERROR);

        Long userId = oldPic.getUserId();
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtil.equals(userId, loginUser.getId()) || userService.isAdmin(loginUser)){
            boolean b = pictureService.removePicture(oldPic);
            ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        }else {
            throw new BusinessException(ErrorCode.NO_AUTH);
        }

        return ResultUtils.success(true);
    }

    @GetMapping("/get")
    public BaseResponse<PictureVO> getPictureById(Long id){
        if (ObjectUtil.isNull(id)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        PictureVO pictureVO = pictureService.getPictureById(id);
        return ResultUtils.success(pictureVO);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> getPictureList(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        QueryWrapper<Picture> queryWrapper = pictureService.getQueryWrapper(pictureQueryRequest, userService.isAdmin(loginUser));

        int pageNum = pictureQueryRequest.getPageNum();
        int pageSize = pictureQueryRequest.getPageSize();

        Page<Picture> page = pictureService.page(new Page<>(pageNum, pageSize), queryWrapper);
        List<Picture> records = page.getRecords();
        List<PictureVO> pictureVOS = pictureService.pictureListToVO(records);

        Page<PictureVO> pageVO = new Page<>(pageNum, pageSize, page.getTotal());
        pageVO.setRecords(pictureVOS);

        return ResultUtils.success(pageVO);
    }


    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updatePicture(@RequestBody PictureUpdateRequest pictureUpdateRequest){
        ThrowUtils.throwIf(pictureUpdateRequest == null, ErrorCode.PARAMS_ERROR);

        Picture picture = new Picture();
        BeanUtil.copyProperties(pictureUpdateRequest, picture);
        Long id = picture.getId();
        ThrowUtils.throwIf(id == null, ErrorCode.PARAMS_ERROR);

        Picture oldPic = pictureService.getById(id);
        ThrowUtils.throwIf(oldPic == null, ErrorCode.NULL_ERROR);

        boolean b = pictureService.updateById(picture);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);

        return ResultUtils.success(true);
    }

    @PostMapping("/edit")
    public BaseResponse<Boolean> editPicture(@RequestBody PictureEditRequest pictureEditRequest,
                                             HttpServletRequest request){
        // 判断请求不为空，且要修改的图片id不为空
        ThrowUtils.throwIf(pictureEditRequest == null || pictureEditRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        // 从数据库查询，要修改的图片是否存在
        Picture oldPic = pictureService.getById(pictureEditRequest.getId());
        User loginUser = userService.getLoginUser(request);
        // 如果图片存在，则修改图片记录
        if (oldPic != null){
            // 仅本人和管理员可编辑
            boolean editable = !userService.isAdmin(loginUser) && loginUser.getId().equals(oldPic.getUserId());
            ThrowUtils.throwIf(!editable, ErrorCode.NO_AUTH);
            Picture newPic = new Picture();
            BeanUtil.copyProperties(pictureEditRequest, newPic);
            newPic.setEditTime(new Date());
            boolean b = pictureService.updateById(newPic);
            ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);
        }
        // 返回修改结果
        return ResultUtils.success(true);
    }



}