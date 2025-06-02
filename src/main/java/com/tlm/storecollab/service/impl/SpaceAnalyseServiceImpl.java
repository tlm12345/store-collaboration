package com.tlm.storecollab.service.impl;

import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.constant.CapacityUnit;
import com.tlm.storecollab.exception.BusinessException;
import com.tlm.storecollab.mapper.SpaceMapper;
import com.tlm.storecollab.model.dto.space.analyse.SpaceAnalyseRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceTopNCapacityUsedMostRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceUserUploadBehaviorAnalyseRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.space.analyse.*;
import com.tlm.storecollab.service.PictureService;
import com.tlm.storecollab.service.SpaceAnalyseService;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
* @author 12483
* @description 针对表【space(空间), picture(图片)】的数据库操作分析Service实现
*/
@Service
public class SpaceAnalyseServiceImpl extends ServiceImpl<SpaceMapper, Space>
    implements SpaceAnalyseService{

    @Resource
    private UserService userService;

    @Resource
    private SpaceService spaceService;

    @Resource
    private PictureService pictureService;


    @Override
    public void checkAuthForSpaceAnalyse(SpaceAnalyseRequest request, User loginUser) {
        // 校验请求参数不为空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 获取请求参数的所有值
        Long spaceId = request.getSpaceId();
        Boolean queryAll = request.getQueryAll();
        Boolean queryPublic = request.getQueryPublic();
        // 如果用户分析全空间或公共空间，则必须为管理员，否则抛出无权限异常
        if (queryAll != null && queryAll){
           ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH);
        }
        if (queryPublic != null && queryPublic){
           ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH);
        }
        // 如果用户分析私有空间，则必须为空间创建人或者是管理员，否则抛出无权限异常
        if (spaceId != null){
            Space space = spaceService.getById(spaceId);
            ThrowUtils.throwIf(space == null, ErrorCode.PARAMS_ERROR, "指定的空间不存在");

            // 如果用户是管理员，权限校验通过，函数直接返回
            if (userService.isAdmin(loginUser)){
                return;
            }
            // 如果用户不是管理员，校验用户是否用空间权限
            spaceService.validUserHasPrivateSpaceAuth(loginUser.getId(), space);
        }

    }

    @Override
    public void setQueryWrapper(SpaceAnalyseRequest request, QueryWrapper<Picture> queryWrapper) {
        // 获取所有请求参数，然后 设置查询条件
        Long spaceId = request.getSpaceId();
        Boolean queryAll = request.getQueryAll();
        Boolean queryPublic = request.getQueryPublic();

        // 如果查询全空间
        if (Optional.ofNullable(queryAll).orElse(false)){
            return;
        }

        // 如果只查询公共空间
        if (Optional.ofNullable(queryPublic).orElse(false)){
            queryWrapper.isNull("spaceId");
            return;
        }

        // 如果查询私人空间
        if (spaceId != null){
            queryWrapper.eq("spaceId", spaceId);
            return;
        }

        throw new BusinessException(ErrorCode.PARAMS_ERROR, "未指定任何查询范围参数");

    }

    @Override
    public SpaceUsedInfoVO queryUserPrivateSpaceCapacityInfo(SpaceAnalyseRequest request, User loginUser) {
        // 校验请求参数不为空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 判断用户是否具有相关请求参数的权限
        checkAuthForSpaceAnalyse(request, loginUser);
        // 查询私人空间信息，获取已使用容量，最大容量，已有图片数，最大图片数
        Long spaceId = request.getSpaceId();
        ThrowUtils.throwIf(spaceId == null, ErrorCode.PARAMS_ERROR, "用户空间ID不能为空");
        Space space = spaceService.getById(spaceId);

        Long spaceTotalCount = space.getSpaceTotalCount();
        Long spaceSizeUsed = space.getSpaceSizeUsed();
        Long spaceMaxCount = space.getSpaceMaxCount();
        Long spaceMaxSize = space.getSpaceMaxSize();

        // 计算比例
        Double spaceUsedRatio = spaceSizeUsed.doubleValue() * 100.0 / spaceMaxSize.doubleValue();
        Double spaceCountRatio = spaceTotalCount.doubleValue() * 100.0 / spaceMaxCount.doubleValue();
        spaceUsedRatio = NumberUtil.round(spaceUsedRatio, 3).doubleValue();
        spaceCountRatio = NumberUtil.round(spaceCountRatio, 3).doubleValue();

        // 构建返回VO，逐个设置值
        SpaceUsedInfoVO spaceUsedInfoVO = new SpaceUsedInfoVO();
        spaceUsedInfoVO.setSpaceUsedRatio(spaceUsedRatio);
        spaceUsedInfoVO.setSpaceCountRatio(spaceCountRatio);
        spaceUsedInfoVO.setSpaceMaxCount(spaceMaxCount);
        spaceUsedInfoVO.setSpaceMaxSize(spaceMaxSize);
        spaceUsedInfoVO.setSpaceSizeUsed(spaceSizeUsed);
        spaceUsedInfoVO.setSpaceTotalCount(spaceTotalCount);
        return spaceUsedInfoVO;
    }

    @Override
    public List<SpaceCategoryAnalyseVO> querySpaceCategoryAnalyseInfo(SpaceAnalyseRequest request, User loginUser) {
        // 校验参数非空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 校验权限
        checkAuthForSpaceAnalyse(request, loginUser);
        // 构造查询条件， 根据类别分组，聚合，需要注意，值查询需要的字段
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        setQueryWrapper(request, pictureQueryWrapper);

        pictureQueryWrapper.select("category", "count(*) as categoryTotalCount", "sum(picSize) as spaceSizeUsed");
        pictureQueryWrapper.groupBy("category");

        List<Map<String, Object>> maps = pictureService.getBaseMapper().selectMaps(pictureQueryWrapper);
        List<SpaceCategoryAnalyseVO> resultList = maps.stream().map(map -> {
            SpaceCategoryAnalyseVO spaceCategoryAnalyseVO = new SpaceCategoryAnalyseVO();
            Set<Map.Entry<String, Object>> entries = map.entrySet();
            entries.forEach(entry -> {
                String key = entry.getKey();
                Object value = entry.getValue();
                switch (key) {
                    case "category":
                        if (value != null) {
                            spaceCategoryAnalyseVO.setCategory(value.toString());
                        }
                        break;
                    case "categoryTotalCount":
                        spaceCategoryAnalyseVO.setSpaceTotalCount(Long.valueOf(value.toString()));
                        break;
                    case "spaceSizeUsed":
                        spaceCategoryAnalyseVO.setSpaceSizeUsed(Long.valueOf(value.toString()));
                    default:
                        break;
                }
            });
            return spaceCategoryAnalyseVO;
        }).collect(Collectors.toList());
        // 进行分析计算
        return resultList;
    }

    @Override
    public List<SpaceTagAnalyseVO> querySpaceTagAnalyseInfo(SpaceAnalyseRequest request, User loginUser) {
        // 校验参数非空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 校验权限
        checkAuthForSpaceAnalyse(request, loginUser);
        // 构造查询条件， 根据类别分组，聚合，需要注意，值查询需要的字段
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        setQueryWrapper(request, pictureQueryWrapper);

        pictureQueryWrapper.select("tags");

        List<Object> objects = pictureService.getBaseMapper().selectObjs(pictureQueryWrapper);
        Map<String, Long> tagAmountMap = objects.stream().filter(ObjUtil::isNotNull)
                .flatMap(obj -> JSONUtil.toList(obj.toString(), String.class).stream())
                .collect(Collectors.groupingBy(tag -> tag, Collectors.counting()));
        // 根据出现次数排序
        List<Map.Entry<String, Long>> tagAmountMapSorted = tagAmountMap.entrySet().stream()
                .sorted((o1, o2) -> Long.compare(o2.getValue(), o1.getValue())).collect(Collectors.toList());
        List<SpaceTagAnalyseVO> resList = tagAmountMapSorted.stream().map(entry -> new SpaceTagAnalyseVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        // 进行分析计算
        return resList;
    }

    @Override
    public List<SpaceSizeAnalyseVO> querySpaceSizeAnalyseInfo(SpaceAnalyseRequest request, User loginUser) {
        // 校验参数
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 构造查询条件，根据请求设定查询范围
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        setQueryWrapper(request, pictureQueryWrapper);
        // 选择需要查询的字段
        pictureQueryWrapper.select("picSize");
        // 统计不同范围的图片数量
        List<Object> objects = pictureService.getBaseMapper().selectObjs(pictureQueryWrapper);

        Map<String, Long> sizeAmountMap = new LinkedHashMap<>();
        objects.stream().filter(ObjUtil::isNotNull)
                .map(obj -> Long.valueOf(obj.toString()))
                .forEach(size -> {
                    if ( size < 100 * CapacityUnit.KB) {
                        sizeAmountMap.put("<100KB", sizeAmountMap.getOrDefault("<100KB", 0L) + 1);
                    }else if (size >= 100 * CapacityUnit.KB && size < 1 * CapacityUnit.MB){
                        sizeAmountMap.put("100KB-1MB", sizeAmountMap.getOrDefault("100KB-1MB", 0L) + 1);
                    }else if (size >= 1 * CapacityUnit.MB && size < 10 * CapacityUnit.MB) {
                        sizeAmountMap.put("1MB-10MB", sizeAmountMap.getOrDefault("1MB-10MB", 0L) + 1);
                    }else {
                        sizeAmountMap.put(">10MB", sizeAmountMap.getOrDefault(">10MB", 0L) + 1);
                    }
                });

        // 构造返回对象
        List<SpaceSizeAnalyseVO> spaceSizeAnalyseVOList = sizeAmountMap.entrySet().stream()
                .map(entry -> new SpaceSizeAnalyseVO(entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());

        return spaceSizeAnalyseVOList;
    }

    @Override
    public List<SpaceUserUploadBehaviorAnalyseVO> queryUserUploadBehaviorAnalyseInfo(SpaceUserUploadBehaviorAnalyseRequest request, User loginUser) {
        // 校验请求参数不为空
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        // 校验权限
        checkAuthForSpaceAnalyse(request, loginUser);
        // 构造查询条件，根据请求设定空间查询范围
        QueryWrapper<Picture> pictureQueryWrapper = new QueryWrapper<>();
        setQueryWrapper(request, pictureQueryWrapper);
        // 根据请求中的时间尺度信息，对不同尺度的图片进行聚合
        String timeDimension = request.getTimeDimension();
        switch (timeDimension) {
            case "day":
                // 根据图片表的createTime字段来分组，统计不同日期的图片数量
                pictureQueryWrapper.select("date_format(createTime, '%Y-%m-%d') as period, count(*) as count");
                break;
            case "week":
                // 根据图片表的createTime字段来分组，统计不同星期的图片数量
                pictureQueryWrapper.select("yearweek(createTime) as period, count(*) as count");
                break;
            case "month":
                // 根据图片表的createTime字段来分组，统计不同月份的图片数量
                pictureQueryWrapper.select("date_format(createTime, '%Y-%m') as period, count(*) as count");
                break;
            default:
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "时间尺度参数错误, 不支持的时间尺度");
        }
        List<Map<String, Object>> maps = pictureService.getBaseMapper().selectMaps(pictureQueryWrapper);
        List<SpaceUserUploadBehaviorAnalyseVO> resList = maps.stream()
                .map(map -> new SpaceUserUploadBehaviorAnalyseVO((String) map.get("period"), Long.valueOf(map.get("count").toString())))
                .collect(Collectors.toList());
        // 构造返回对象
        return resList;
    }

    @Override
    public List<Space> querySpaceTopNInfo(SpaceTopNCapacityUsedMostRequest request, User loginUser) {
        // 校验请求参数不为空， 如果为空，设置topN为10
        ThrowUtils.throwIf(request == null || request.getTopN() <= 0, ErrorCode.PARAMS_ERROR);
        // 当前用户必须为管理员，否则抛出异常
        ThrowUtils.throwIf(!userService.isAdmin(loginUser), ErrorCode.NO_AUTH);

        // 查询空间表，根据spaceSizeUsed字段进行降序排序，然后取出前topN个数据
        // 返回结果
        return spaceService.list(new QueryWrapper<Space>().orderByDesc("spaceSizeUsed").last("limit " + request.getTopN()));
    }
}




