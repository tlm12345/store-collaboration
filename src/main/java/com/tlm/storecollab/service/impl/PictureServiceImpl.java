package com.tlm.storecollab.service.impl;
import java.time.Duration;
import java.util.ArrayList;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.config.CosClientConfig;
import com.tlm.storecollab.constant.CacheConstant;
import com.tlm.storecollab.constant.PictureConstant;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.manager.upload.PictureUpload;
import com.tlm.storecollab.manager.upload.PictureUploadTemplate;
import com.tlm.storecollab.manager.upload.UrlUpload;
import com.tlm.storecollab.model.dto.file.UploadPictureResult;
import com.tlm.storecollab.model.dto.picture.PictureQueryRequest;
import com.tlm.storecollab.model.dto.picture.PictureReviewRequest;
import com.tlm.storecollab.model.dto.picture.UploadPictureByBatchRequest;
import com.tlm.storecollab.model.dto.picture.UploadPictureRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.PictureReviewStatusEnum;
import com.tlm.storecollab.model.vo.PictureVO;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.mapper.PictureMapper;
import com.tlm.storecollab.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
* @author tlm
* @description 针对表【picture(图片)】的数据库操作Service实现
* @createDate 2025-05-15 11:10:31
*/
@Slf4j
@Service
public class PictureServiceImpl extends ServiceImpl<PictureMapper, Picture>
    implements PictureService{

    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private UserService userService;

    @Resource
    private PictureUpload pictureUpload;

    @Resource
    private UrlUpload urlUpload;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    Cache<String, String> LOCAL_CACHE = Caffeine.newBuilder()
            .maximumSize(10_000)
            .expireAfterWrite(Duration.ofMinutes(5))
            .build();


    @Override
    public PictureVO uploadPicture(Object inputSource, UploadPictureRequest uploadPictureRequest, User loginUser) {
        // 校验登录
        ThrowUtils.throwIf(ObjectUtil.isEmpty(loginUser), ErrorCode.NOT_LOGIN);
        // 校验图片文件和上传请求参数是否为空
        // 如果uploadPictureRequest为空，则默认为只上传图片,此时图片必须不为空
        // 如果uplaodPictureRequest不为空，则默认为完善图片记录,此时图片可以为空
        if (uploadPictureRequest == null){
            ThrowUtils.throwIf(ObjectUtil.isEmpty(inputSource), ErrorCode.NULL_ERROR, "请上传图片");
        }

        // 根据用户是否上传图片记录id来判断，本次请求是只上传图片，还是完善图片记录
        Long picId = uploadPictureRequest.getId();
        Long userId = loginUser.getId();
        Picture picture = new Picture();

        PictureUploadTemplate pictureUploadTemplate = pictureUpload;
        if (inputSource instanceof String){
            pictureUploadTemplate = urlUpload;
        }
        // 如果只上传图片, 创建图片记录
        if (picId == null){
            UploadPictureResult uploadPictureResult = pictureUploadTemplate.uploadPicture(inputSource, "public");
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

        if (userService.isAdmin(loginUser)){
            picture.setViewTime(new Date());
            picture.setViewer(loginUser.getId());
            picture.setViewStatus(PictureReviewStatusEnum.PASS.getValue());
            picture.setViewMessage(PictureConstant.ADMIN_CREATE);
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
        queryWrapper.like(StrUtil.isNotBlank(name), "name", name);
        queryWrapper.like(StrUtil.isNotBlank(introduction), "introduction", introduction);
        queryWrapper.eq(StrUtil.isNotBlank(category), "category", category);
        // 拼接标签查询条件
        if (tags != null){
            queryWrapper.and(eq -> {
                tags.forEach(tag -> {
                    eq.like("tags", "\"" + tag + "\"");
                });
            });
        }

        if (isAdmin) {
            if (StrUtil.isNotBlank(searchText)){
                queryWrapper.and(eq -> eq.like( "name", searchText).like("introduction", searchText));
            }
        }else {
            // 不是管理员，则只能查看到，审核通过的图片
            queryWrapper.eq("viewStatus", PictureReviewStatusEnum.PASS.getValue());
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
        List<User> userList = null;
        if (CollUtil.isEmpty(userIdSet)){
            return picVOList;
        }else {
            userList = userService.list(new QueryWrapper<User>().in("id", userIdSet));
        }
        Map<Long, List<User>> id2UserMap = userList.stream().collect(Collectors.groupingBy(User::getId));
        picVOList.forEach(picVO -> {
            Long userId = picVO.getUserId();
            picVO.setUserVO(userService.getUserVO(id2UserMap.get(userId).get(0)));
        });

        return picVOList;
    }

    @Override
    public boolean reviewPicture(PictureReviewRequest pictureReviewRequest, User loginUser) {
        // 校验数据是否为空
        ThrowUtils.throwIf(pictureReviewRequest == null || pictureReviewRequest.getId() == null, ErrorCode.NULL_ERROR);

        // 判断是否是管理员在操作
        Boolean isAdmin = userService.isAdmin(loginUser);
        ThrowUtils.throwIf(!isAdmin, ErrorCode.NO_AUTH);

        // 获取请求数据
        Long id = pictureReviewRequest.getId();
        Integer newStatus = pictureReviewRequest.getViewStatus();
        String viewMessage = pictureReviewRequest.getViewMessage();

        // 判断图片是否在数据库中
        Picture oldPic = this.getById(id);
        ThrowUtils.throwIf(oldPic == null, ErrorCode.PARAMS_ERROR, "图片不存在");

        // 审核状态不能无变化， 也不能从通过和拒绝转换到待审核
        Integer oldStatus = oldPic.getViewStatus();
        boolean notChange = Objects.equals(oldStatus, newStatus);
        boolean passOrRejected2Reviewing = (!PictureReviewStatusEnum.REVIEWING.getValue().equals(oldStatus)
                && PictureReviewStatusEnum.REVIEWING.getValue().equals(newStatus));
        ThrowUtils.throwIf(notChange || passOrRejected2Reviewing, ErrorCode.PARAMS_ERROR, "不支持的状态流转");

        // 修改图片状态以及审核人id，审核时间，审核原因，并返回修改结果
        Picture newPic = new Picture();
        BeanUtil.copyProperties(pictureReviewRequest, newPic);
        newPic.setViewer(loginUser.getId());
        newPic.setViewTime(new Date());

        boolean res = this.updateById(newPic);
        ThrowUtils.throwIf(!res, ErrorCode.OPERATION_ERROR);

        return true;
    }

    @Override
    public Integer graspPicturesByBatch(UploadPictureByBatchRequest uploadPictureByBatchRequest, User loginUser) {
        // 校验参数是否为空
        ThrowUtils.throwIf(
                uploadPictureByBatchRequest == null || StrUtil.isBlank(uploadPictureByBatchRequest.getQ()),
                ErrorCode.NULL_ERROR);

        // 获取请求数据
        String q = uploadPictureByBatchRequest.getQ();
        String prefixName = uploadPictureByBatchRequest.getPrefixName();
        Integer count = uploadPictureByBatchRequest.getCount();
        if (StrUtil.isBlank(prefixName)) prefixName = q;

        // 构造查询接口
        String queryInterface = String.format("https://cn.bing.com/images/async?q=%s&count=%s", q, count);
        // 定义成功上传的图片数
        int amountSuccess = 0;
        // 使用Jsoup获取响应
        Document doc = null;
        try {
            doc = Jsoup.connect(queryInterface).get();
        } catch (IOException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "查询失败");
        }
        // 获取所有的img元素
        Elements elements = doc.select("img.mimg");
        // 循环， 获取每个img元素的src属性，然后上传图片
        for (Element element : elements) {
            String fileUrl = element.attr("src");
            if (StrUtil.isBlank(fileUrl)){
                continue;
            }
            // 清晰url，删除不必要的查询参数
            int queryMarkIndex = fileUrl.indexOf("?");
            if (queryMarkIndex == -1) {
                // 没找到?, 处理下一个元素
                continue;
            }
            fileUrl = fileUrl.substring(0, queryMarkIndex);

            UploadPictureRequest uploadPictureRequest = new UploadPictureRequest();
            uploadPictureRequest.setUrl(fileUrl);
            uploadPictureRequest.setName(prefixName + "-" + (amountSuccess + 1));
            try {
                this.uploadPicture(fileUrl, uploadPictureRequest, loginUser);
            }catch (Exception e){
                log.error("图片上传失败：\n url is : [{}]\n" + e.getMessage(), fileUrl);
                continue;
            }
            amountSuccess++;

            if (amountSuccess >= 30) {
                break;
            }
        }
        // 返回成功上传的图片数
        return amountSuccess;
    }

    @Override
    public Page<PictureVO> getPictureVOListFromCache(PictureQueryRequest pictureQueryRequest, User loginUser) {
        // 校验参数不为空
        ThrowUtils.throwIf(pictureQueryRequest == null, ErrorCode.NULL_ERROR);

        // 判断是否为管理员
        Boolean isAdmin = userService.isAdmin(loginUser);

        // 将查询条件JSON序列化, 同时将isAdmin拼接在json字符串后，以区别管理员缓存和普通用户缓存
        String s = JSONUtil.toJsonStr(pictureQueryRequest);
        String queryKey = s + isAdmin;
        // 使用md5对查询条件进行压缩，以作为查询key来节省空间
        byte[] bytes = DigestUtil.md5(queryKey.getBytes());
        // 构造本地key，用于从caffiene中查询，相较于redis的key更短，减少空间占用
        String localKey = new String(bytes);
        // 构造查询redis的key
        String key = String.format(CacheConstant.CACHE_FOR_PAGE_QUERY, localKey);
        // 从本地缓存中查数据
        String cacheValue = LOCAL_CACHE.getIfPresent(key);
        // local cache hits
        if (StrUtil.isNotBlank(cacheValue)){
            return JSONUtil.toBean(cacheValue, Page.class);
        }
        cacheValue = stringRedisTemplate.opsForValue().get(key);
        // remote cache hits
        if (StrUtil.isNotBlank(cacheValue)){
            // 更新本地缓存
            LOCAL_CACHE.put(key, cacheValue);
            return JSONUtil.toBean(cacheValue, Page.class);
        }
        // cache miss
        QueryWrapper<Picture> queryWrapper = this.getQueryWrapper(pictureQueryRequest, isAdmin);

        Page<Picture> picturePage = this.page(new Page<>(pictureQueryRequest.getPageNum(),
                pictureQueryRequest.getPageSize()),
                queryWrapper);

        long current = picturePage.getCurrent();
        long size = picturePage.getSize();
        long total = picturePage.getTotal();
        List<Picture> records = picturePage.getRecords();
        List<PictureVO> PicVOList = records.stream().map(PictureVO::objToVo).collect(Collectors.toList());

        Page<PictureVO> pictureVOPage = new Page<>(current, size, total);
        pictureVOPage.setRecords(PicVOList);

        // 将该数据缓存
        String valueForCache = JSONUtil.toJsonStr(pictureVOPage);
        // 构建一定范围(10-20min)内的过期时间，避免缓存雪崩
        int randomNumber = RandomUtil.randomInt(10, 20);
        stringRedisTemplate.opsForValue().set(key, valueForCache, randomNumber, TimeUnit.MINUTES);
        LOCAL_CACHE.put(key, valueForCache);
        return pictureVOPage;
    }
}




