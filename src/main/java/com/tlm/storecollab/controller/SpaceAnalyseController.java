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
import com.tlm.storecollab.common.*;
import com.tlm.storecollab.constant.UserConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.model.dto.picture.*;
import com.tlm.storecollab.model.dto.space.analyse.SpaceAnalyseRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceTopNCapacityUsedMostRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceUserUploadBehaviorAnalyseRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.PictureVO;
import com.tlm.storecollab.model.vo.space.analyse.*;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceAnalyseService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.xml.transform.Result;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 空间分析接口
 */
@Slf4j
@RestController
@RequestMapping("/space/analyse")
public class SpaceAnalyseController {

    @Resource
    private UserService userService;

    @Resource
    private PictureService pictureService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private SpaceAnalyseService spaceAnalyseService;

    @GetMapping("/privateAnalyse")
    public BaseResponse<SpaceUsedInfoVO> queryUserPrivateSpaceCapacityInfo(@RequestParam(required = true) Long spaceId,
                                                                           HttpServletRequest request) {
        // 校验空间id的合法性，不能为空，且不能为负数
        ThrowUtils.throwIf(spaceId <= 0, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(request);
        SpaceAnalyseRequest spaceAnalyseRequest = new SpaceAnalyseRequest();
        spaceAnalyseRequest.setSpaceId(spaceId);

        SpaceUsedInfoVO spaceUsedInfoVO = spaceAnalyseService.queryUserPrivateSpaceCapacityInfo(spaceAnalyseRequest, loginUser);
        return ResultUtils.success(spaceUsedInfoVO);
    }

    @PostMapping("/category/analyse")
    public BaseResponse<List<SpaceCategoryAnalyseVO>> querySpaceCategoryAnalyseInfo(@RequestBody SpaceAnalyseRequest request,
                                                                             HttpServletRequest httpServletRequest) {
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceCategoryAnalyseVO> spaceCategoryAnalyseVOList = spaceAnalyseService.querySpaceCategoryAnalyseInfo(request, loginUser);
        return ResultUtils.success(spaceCategoryAnalyseVOList);
    }

    @PostMapping("/tag/analyse")
    public BaseResponse<List<SpaceTagAnalyseVO>> querySpaceTagAnalyseInfo(@RequestBody SpaceAnalyseRequest request,
                                                                             HttpServletRequest httpServletRequest) {
        // 校验请求
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceTagAnalyseVO> spaceTagAnalyseVOList = spaceAnalyseService.querySpaceTagAnalyseInfo(request, loginUser);
        return ResultUtils.success(spaceTagAnalyseVOList);
    }

    @PostMapping("/size/analyse")
    public BaseResponse<List<SpaceSizeAnalyseVO>> querySpaceSizeAnalyseInfo(@RequestBody SpaceAnalyseRequest request,
                                                                            HttpServletRequest httpServletRequest) {
        // 校验请求
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceSizeAnalyseVO> spaceSizeAnalyseVOList = spaceAnalyseService.querySpaceSizeAnalyseInfo(request, loginUser);
        return ResultUtils.success(spaceSizeAnalyseVOList);
    }

    @PostMapping("/user/analyse")
    public BaseResponse<List<SpaceUserUploadBehaviorAnalyseVO>> queryUserUploadBehaviorAnalyseInfo(@RequestBody SpaceUserUploadBehaviorAnalyseRequest request,
                                                                                                   HttpServletRequest httpServletRequest) {
        // 校验请求
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<SpaceUserUploadBehaviorAnalyseVO> spaceUserUploadBehaviorAnalyseVOList = spaceAnalyseService.queryUserUploadBehaviorAnalyseInfo(request, loginUser);
        return ResultUtils.success(spaceUserUploadBehaviorAnalyseVOList);
    }

    @PostMapping("/space/topN")
    public BaseResponse<List<Space>> querySpaceTopNInfo(@RequestBody SpaceTopNCapacityUsedMostRequest request,
                                                       HttpServletRequest httpServletRequest) {
        // 校验请求
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        User loginUser = userService.getLoginUser(httpServletRequest);
        List<Space> spaceList = spaceAnalyseService.querySpaceTopNInfo(request, loginUser);
        return ResultUtils.success(spaceList);
    }

}