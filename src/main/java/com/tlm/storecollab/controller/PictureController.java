package com.tlm.storecollab.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.tlm.storecollab.annotation.AuthCheck;
import com.tlm.storecollab.api.imagesearch.ImageSearchFacade;
import com.tlm.storecollab.api.imagesearch.model.ImageSearchResult;
import com.tlm.storecollab.common.BaseResponse;
import com.tlm.storecollab.common.DeleteRequest;
import com.tlm.storecollab.common.ResultUtils;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.model.dto.picture.*;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
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
 * 图片接口
 */
@Slf4j
@RestController
@RequestMapping("/picture")
public class PictureController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @PostMapping("/upload")
    public BaseResponse<PictureVO> uploadPicture(@RequestPart("file") MultipartFile multipartFile,
                                                 UploadPictureRequest uploadPictureRequest,
                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(multipartFile, uploadPictureRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }
    @PostMapping("/upload/url")
    public BaseResponse<PictureVO> uploadPictureByUrl(@RequestBody UploadPictureRequest uploadPictureRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(uploadPictureRequest == null || StrUtil.isBlank(uploadPictureRequest.getUrl()), ErrorCode.NULL_ERROR);
        String url = uploadPictureRequest.getUrl();
        User loginUser = userService.getLoginUser(request);
        PictureVO pictureVO = pictureService.uploadPicture(url, uploadPictureRequest, loginUser);
        return ResultUtils.success(pictureVO);
    }

    @PostMapping("/delete")
    public BaseResponse<Boolean> deletePicture(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request){
        if (ObjectUtil.isNull(deleteRequest) || ObjectUtil.isNull(deleteRequest.getId())) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        Long picId = deleteRequest.getId();
        Picture oldPic = pictureService.getById(picId);
        ThrowUtils.throwIf(oldPic == null, ErrorCode.NULL_ERROR);

        Long spaceId = oldPic.getSpaceId();
        Long userId = oldPic.getUserId();
        User loginUser = userService.getLoginUser(request);
        if (ObjectUtil.equals(userId, loginUser.getId()) || userService.isAdmin(loginUser)){
            if (ObjUtil.isNotNull(spaceId)){
                // 如果是删除用户私有空间的图片
                pictureService.removePrivatePictureAndReleaseSpace(loginUser.getId(), picId);
                return ResultUtils.success(true);
            }
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

    /**
     * 根据颜色搜索图片
     * @param searchPictureByColorRequest
     * @param request
     * @return
     */
    @PostMapping("/search/byColor")
    public BaseResponse<List<PictureVO>> searchImageByColor(@RequestBody SearchPictureByColorRequest searchPictureByColorRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(searchPictureByColorRequest == null || StrUtil.isBlank(searchPictureByColorRequest.getPicAve()), ErrorCode.NULL_ERROR);
        User loginUser = userService.getLoginUser(request);
        List<PictureVO> imageSearchResults = pictureService.searchPictureByColor(searchPictureByColorRequest, loginUser);
        return ResultUtils.success(imageSearchResults);
    }

    /**
     * **批量**修改图片信息
     */
    @PostMapping("/edit/batch")
    public BaseResponse<Boolean> editPictureByBatch(@RequestBody PictureEditByBatchRequest pictureEditByBatchRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(pictureEditByBatchRequest == null || pictureEditByBatchRequest.getPictureIdList() == null, ErrorCode.PARAMS_ERROR);
        boolean b = pictureService.editPictureByBatch(pictureEditByBatchRequest, userService.getLoginUser(request));
        return ResultUtils.success(b);
    }

    @PostMapping("/list/page/vo")
    public BaseResponse<Page<PictureVO>> getPicturePage(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        // 判断是否查询私人空间的图片
        boolean queryPirvateSpace = pictureQueryRequest.getQueryPrivateSpace();
        if (queryPirvateSpace){
            // 设置请求为当前登录用户的私人空间id
            Space space = spaceService.lambdaQuery().eq(Space::getUserId, loginUser.getId()).one();
            pictureQueryRequest.setSpaceId(space.getId());
        }
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

    @PostMapping("/list/page/vo/cache")
    public BaseResponse<Page<PictureVO>> getPictureListFromCache(@RequestBody PictureQueryRequest pictureQueryRequest,
                                                        HttpServletRequest request){
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.NULL_ERROR);

        Page<PictureVO> pictureVOListFromCache = pictureService.getPictureVOListFromCache(pictureQueryRequest, userService.getLoginUser(request));

        return ResultUtils.success(pictureVOListFromCache);
    }
    @GetMapping("/search/byUrl")
    public BaseResponse<List<ImageSearchResult>> searchImageByImageUrl(@RequestParam String url, HttpServletRequest request){
        // 校验查询参数
        ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.PARAMS_ERROR);
        // 用户必须登录
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(loginUser == null, ErrorCode.NOT_LOGIN);

        List<ImageSearchResult> imageSearchResults = ImageSearchFacade.getImageSearchResult(url);
        return ResultUtils.success(imageSearchResults);
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

    @GetMapping("/tag_category")
    public BaseResponse<PictureTagCategory> listPictureTagCategory(){
        List<String> tags = Arrays.asList("校招", "Java", "校园", "大一", "大二");
        List<String> categories = Arrays.asList("职场", "校园", "生活", "学习");
        PictureTagCategory pictureTagCategory = new PictureTagCategory();
        pictureTagCategory.setCategories(categories);
        pictureTagCategory.setTags(tags);
        return ResultUtils.success(pictureTagCategory);
    }

    @PostMapping("/review")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> reviewPicture(@RequestBody PictureReviewRequest pictureReviewRequest, HttpServletRequest request){
        User loginUser = userService.getLoginUser(request);
        ThrowUtils.throwIf(pictureReviewRequest == null || pictureReviewRequest.getId() == null, ErrorCode.PARAMS_ERROR);

        boolean b = pictureService.reviewPicture(pictureReviewRequest, loginUser);
        return ResultUtils.success(b);
    }

    @PostMapping("/upload/batch")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Integer> uploadPicturesByBatch(@RequestBody UploadPictureByBatchRequest uploadPictureByBatchRequest, HttpServletRequest request){
        ThrowUtils.throwIf(uploadPictureByBatchRequest == null, ErrorCode.NULL_ERROR);

        User loginUser = userService.getLoginUser(request);
        Integer count = pictureService.graspPicturesByBatch(uploadPictureByBatchRequest, loginUser);
        return ResultUtils.success(count);
    }
}