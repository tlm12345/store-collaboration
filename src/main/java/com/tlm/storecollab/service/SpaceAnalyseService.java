package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceAnalyseRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceTopNCapacityUsedMostRequest;
import com.tlm.storecollab.model.dto.space.analyse.SpaceUserUploadBehaviorAnalyseRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.space.analyse.*;

import java.util.List;

/**
* @author 12483
* @description 针对表【space(空间), picture(图片)】的数据库分析操作Service
*/
public interface SpaceAnalyseService extends IService<Space> {
    /**
     * 为用户的空间分析请求进行权限校验
     * @param request
     * @param loginUser
     */
    public void checkAuthForSpaceAnalyse(SpaceAnalyseRequest request, User loginUser);

    /**
     * 根据空间分析请求参数设置querywrapper的查询范围
     * @param request
     * @param queryWrapper
     */
    public void setQueryWrapper(SpaceAnalyseRequest request, QueryWrapper<Picture> queryWrapper);

    /**
     * 查询用户私人空间的空间占用量和资源数量占用量
     * @param request
     * @param loginUser
     * @return
     */
    public SpaceUsedInfoVO queryUserPrivateSpaceCapacityInfo(SpaceAnalyseRequest request, User loginUser);

    /**
     * 查询空间中不同类别的图片数量和占用空间大小
     * @param request
     * @param loginUser
     * @return
     */
    public List<SpaceCategoryAnalyseVO> querySpaceCategoryAnalyseInfo(SpaceAnalyseRequest request, User loginUser);

    /**
     * 查询空间中不同标签下的图片数量
     * @param request
     * @param loginUser
     * @return
     */
    public List<SpaceTagAnalyseVO> querySpaceTagAnalyseInfo(SpaceAnalyseRequest request, User loginUser);

    /**
     * 查询空间中不同大小的图片数量
     * @param request
     * @param loginUser
     * @return
     */
    public List<SpaceSizeAnalyseVO> querySpaceSizeAnalyseInfo(SpaceAnalyseRequest request, User loginUser);

    /**
     * 分析用户在不同空间下的上传数量行为
     */
    public List<SpaceUserUploadBehaviorAnalyseVO> queryUserUploadBehaviorAnalyseInfo(SpaceUserUploadBehaviorAnalyseRequest request, User loginUser);

    /**
     * 管理员获取空间使用量最高的前N个空间
     */
    public List<Space> querySpaceTopNInfo(SpaceTopNCapacityUsedMostRequest request, User loginUser);

}
