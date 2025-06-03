package com.tlm.storecollab.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tlm.storecollab.common.DeleteRequest;
import com.tlm.storecollab.model.dto.spaceuser.*;
import com.tlm.storecollab.model.entity.SpaceUser;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.vo.SpaceUserVO;

import java.util.List;

/**
* @author tlm
* @description 针对表【space_user(空间成员)】的数据库操作Service
* @createDate 2025-06-03 13:45:46
*/
public interface SpaceUserService extends IService<SpaceUser> {

    /**
     * 添加空间成员
     * @param addSpaceUserRequest
     * @param loginUser
     * @return
     */
    public Long addSpaceUser(AddSpaceUserRequest addSpaceUserRequest, User loginUser);

    /**
     * 删除空间成员
     * @param deleteRequest
     * @param loginUser
     */
    public void deleteSpaceUser(DeleteRequest deleteRequest, User loginUser);

    /**
     * 更新空间成员
     * @param updateSpaceUserRequest
     * @param loginUser
     * @return
     */
    public boolean updateSpaceUser(UpdateSpaceUserRequest updateSpaceUserRequest, User loginUser);

    /**
     * 根据空间id和用户id查询空间成员
     * @param spaceId
     * @param userId
     * @return
     */
    public SpaceUser getSpaceUser(Long spaceId, Long userId);
    public SpaceUser getSpaceUser(GetSpaceUserRequest getSpaceUserRequest);

    /**
     * 根据空间id查询空间成员
     * @param spaceId
     * @return
     */
    public List<SpaceUser> getAllSpaceUser(Long spaceId);
    public List<SpaceUser> getAllSpaceUser(GetAllSpaceUserRequest getAllSpaceUserRequest);

    /**
     * 根据查询条件查询成员
     * @param querySpaceUserRequest
     * @return
     */
    public List<SpaceUser> querySpaceUserWithCondition(QuerySpaceUserRequest querySpaceUserRequest);

    /**
     * 将SpaceUser实体类转换为相应的VO类
     * @param spaceUser
     * @return
     */
    public SpaceUserVO objToVo(SpaceUser spaceUser);

    /**
     * 根据查询请求构造querywrapper
     */
    public QueryWrapper<SpaceUser> getQueryWrapper(QuerySpaceUserRequest querySpaceUserRequest);
}
