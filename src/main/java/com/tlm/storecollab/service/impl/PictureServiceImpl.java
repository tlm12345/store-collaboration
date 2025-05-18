package com.tlm.storecollab.service.impl;
import java.util.*;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.config.CosClientConfig;
import com.tlm.storecollab.manager.FileManager;
import com.tlm.storecollab.model.dto.file.UploadPictureResult;
import com.tlm.storecollab.model.dto.picture.PictureQueryRequest;
import com.tlm.storecollab.model.dto.picture.UploadPictureRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.PictureVO;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.mapper.PictureMapper;
import com.tlm.storecollab.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
* @author tlm
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-05-15 11:10:31
*/
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private CosClientConfig cosClientConfig;
    @Resource
    private FileManager fileManager;

    @Resource
    private UserService userService;


    @Override
    public PictureVO uploadPicture(MultipartFile multipartFile, UploadPictureRequest uploadPictureRequest, User loginUser) {
        // 校验登录
        ThrowUtils.throwIf(ObjectUtil.isEmpty(loginUser), ErrorCode.NOT_LOGIN);
        // 校验图片文件和上传请求参数是否为空
        // 如果uploadPictureRequest为空，则默认为只上传图片,此时图片必须不为空
        // 如果uplaodPictureRequest不为空，则默认为完善图片记录,此时图片可以为空
        if (uploadPictureRequest == null){
            ThrowUtils.throwIf(ObjectUtil.isEmpty(multipartFile), ErrorCode.NULL_ERROR, "请上传图片");
        }

        // 根据用户是否上传图片记录id来判断，本次请求是只上传图片，还是完善图片记录
        Long picId = uploadPictureRequest.getId();
        Long userId = loginUser.getId();
        Picture picture = new Picture();
        // 如果只上传图片, 创建图片记录
        if (picId == null){
            UploadPictureResult uploadPictureResult = fileManager.uploadPicture(multipartFile, "public");
            picture.setUrl(uploadPictureResult.getUrl());
            picture.setName(uploadPictureResult.getPicName());
            picture.setPicSize(uploadPictureResult.getPicSize());
            picture.setPicWidth(uploadPictureResult.getPicWidth());
            picture.setPicHeight(uploadPictureResult.getPicHeight());
            picture.setPicScale(uploadPictureResult.getPicScale());
            picture.setPicFormat(uploadPictureResult.getPicFormat());
            picture.setUserId(userId);
        }else {
            // 如果完善图片记录
                // 根据图片id判断，是否已在数据库中有记录
            Picture oldPic = this.getById(picId);
            ThrowUtils.throwIf(ObjectUtil.isEmpty(oldPic), ErrorCode.NULL_ERROR, "图片不存在(未上传过)");
            //没有， 抛异常
                    // 有， 更新
            picture.setId(picId);
            picture.setCategory(uploadPictureRequest.getCategory());
            picture.setName(uploadPictureRequest.getName());
            picture.setIntroduction(uploadPictureRequest.getIntroduction());
            // 利用hutool包的JSONUtil将List<String>类型数据转为json字符串
            List<String> tags1 = uploadPictureRequest.getTags();
            if (tags1 == null) tags1 = Arrays.asList();
            picture.setTags(JSONUtil.toJsonStr(tags1));
        }

        // 返回最新的图片记录
        boolean b = this.saveOrUpdate(picture);
        ThrowUtils.throwIf(!b, ErrorCode.SYSTEM_ERROR);

        return PictureVO.objToVo(picture);
    }

    @Override
    public PictureVO getPictureById(Long id) {
        ThrowUtils.throwIf(id == null, ErrorCode.NULL_ERROR);

        Picture pic = this.getById(id);
        PictureVO pictureVO = PictureVO.objToVo(pic);

        Long userId = pic.getUserId();
        if (userId != null){
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            pictureVO.setUserVO(userVO);
        }
        return pictureVO;
    }

    @Override
    public List<PictureVO> getPictureList(PictureQueryRequest pictureQueryRequest, User loginUser) {
        ThrowUtils.throwIf(ObjectUtil.isEmpty(pictureQueryRequest), ErrorCode.NULL_ERROR);
        Boolean isAdmin = userService.isAdmin(loginUser);

        QueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureQueryRequest, isAdmin);
        List<Picture> picList = this.list(queryWrapper);
        if (picList == null) return new ArrayList<>();
        List<PictureVO> pictureVOS = this.pictureListToVO(picList);

        return pictureVOS;
    }

    @Override
    public QueryWrapper<Picture> getQueryWrapper(PictureQueryRequest pictureQueryRequest, boolean isAdmin) {
        if (pictureQueryRequest == null) return new QueryWrapper<>();
        String searchText = pictureQueryRequest.getSearchText();
        String name = pictureQueryRequest.getName();
        String introduction = pictureQueryRequest.getIntroduction();
        String category = pictureQueryRequest.getCategory();
        List<String> tags = pictureQueryRequest.getTags();
        Long minPicSize = pictureQueryRequest.getMinPicSize();
        Long maxPicSize = pictureQueryRequest.getMaxPicSize();
        Integer picWidth = pictureQueryRequest.getPicWidth();
        Integer picHeight = pictureQueryRequest.getPicHeight();
        Double picScale = pictureQueryRequest.getPicScale();
        String picFormat = pictureQueryRequest.getPicFormat();
        Long userId = pictureQueryRequest.getUserId();
        Date createTime = pictureQueryRequest.getCreateTime();


        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(StrUtil.isNotBlank(searchText), "name", searchText);
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        // 拼接标签查询条件
        queryWrapper.and(eq -> {
            if (tags != null){
                tags.forEach(tag -> {
                    eq.like("tags", "\"" + tag + "\"");
                });
            }
        });
        if (isAdmin) {
            queryWrapper.and(eq -> eq.like("name", searchText).like("introduction", searchText));
        }
        queryWrapper.ge(minPicSize != null, "picSize", minPicSize);
        queryWrapper.le(maxPicSize != null, "picSize", maxPicSize);
        queryWrapper.eq(picWidth != null, "picWidth", picWidth);
        queryWrapper.eq(picHeight != null, "picHeight", picHeight);
        queryWrapper.eq(picScale != null, "picScale", picScale);
        queryWrapper.eq(StrUtil.isNotBlank(picFormat), "picFormat", picFormat);
        queryWrapper.eq(userId != null, "userId", userId);
        // 创建时间在createTime之后
        queryWrapper.ge(createTime != null, "createTime", createTime);

        return queryWrapper;
    }

    @Override
    public List<PictureVO> pictureListToVO(List<Picture> pictureList) {
        if (pictureList == null) return new ArrayList<PictureVO>();

        List<PictureVO> picVOList = pictureList.stream().map(PictureVO::objToVo).collect(Collectors.toList());
        Set<Long> userIdSet = picVOList.stream().map(PictureVO::getUserId).collect(Collectors.toSet());
        List<User> userList = userService.list(new QueryWrapper<User>().in("userId", userIdSet));
        Map<Long, List<User>> id2UserMap = userList.stream().collect(Collectors.groupingBy(User::getId));
        picVOList.forEach(picVO -> {
            Long userId = picVO.getUserId();
            picVO.setUserVO(userService.getUserVO(id2UserMap.get(userId).get(0)));
        });

        return picVOList;
    }
}




