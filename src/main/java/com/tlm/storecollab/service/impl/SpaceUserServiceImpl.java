package com.tlm.storecollab.service.impl;
import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tlm.storecollab.common.DeleteRequest;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.model.dto.spaceuser.*;
import com.tlm.storecollab.model.entity.Space;
import com.tlm.storecollab.model.entity.SpaceUser;
import com.tlm.storecollab.model.entity.User;
import com.tlm.storecollab.model.enums.SpaceRoleEnum;
import com.tlm.storecollab.model.vo.SpaceUserVO;
import com.tlm.storecollab.model.vo.SpaceVO;
import com.tlm.storecollab.model.vo.UserVO;
import com.tlm.storecollab.service.SpaceService;
import com.tlm.storecollab.service.SpaceUserService;
import com.tlm.storecollab.mapper.SpaceUserMapper;
import com.tlm.storecollab.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Optional;

/**
* @author tlm
* @description 针对表【space_user(空间成员)】的数据库操作Service实现
* @createDate 2025-06-03 13:45:46
*/
@Service
public class SpaceUserServiceImpl extends ServiceImpl<SpaceUserMapper, SpaceUser>
    implements SpaceUserService{

    @Resource
    @Lazy
    private SpaceService spaceService;

    @Resource
    private UserService userService;

    @Override
    public Long addSpaceUser(AddSpaceUserRequest addSpaceUserRequest, User loginUser) {
        // 校验申请参数非空
        ThrowUtils.throwIf(addSpaceUserRequest == null, ErrorCode.NULL_ERROR);
        Long spaceId = addSpaceUserRequest.getSpaceId();
        Long userId = addSpaceUserRequest.getUserId();
        String spaceRole = addSpaceUserRequest.getSpaceRole();
        SpaceRoleEnum spaceRoleEnum = Optional.ofNullable(SpaceRoleEnum.getEnumByValue(spaceRole)).orElse(SpaceRoleEnum.VIEWER);
        // 校验参数非空
        ThrowUtils.throwIf(spaceId == null || userId == null, ErrorCode.NULL_ERROR);

        // 将用户加入到空间成员表中
        SpaceUser spaceUser = new SpaceUser();
        spaceUser.setSpaceId(spaceId);
        spaceUser.setUserId(userId);

        boolean save = this.save(spaceUser);
        ThrowUtils.throwIf(!save, ErrorCode.SYSTEM_ERROR, "添加成员失败");

        return spaceUser.getId();
    }

    @Override
    public void deleteSpaceUser(DeleteRequest deleteRequest, User loginUser) {
        // 校验参数非空
        ThrowUtils.throwIf(deleteRequest == null || deleteRequest.getId() == null, ErrorCode.NULL_ERROR);

        // 直接删除指定id的成员记录
        boolean removeById = this.removeById(deleteRequest.getId());
        ThrowUtils.throwIf(!removeById, ErrorCode.SYSTEM_ERROR, "删除成员失败");
    }

    @Override
    public boolean updateSpaceUser(UpdateSpaceUserRequest updateSpaceUserRequest, User loginUser) {
        // 校验参数非空
        ThrowUtils.throwIf(updateSpaceUserRequest == null || updateSpaceUserRequest.getId() == null, ErrorCode.NULL_ERROR);
        // 获取需要更新的参数
        Long id = updateSpaceUserRequest.getId();
        String spaceRole = updateSpaceUserRequest.getSpaceRole();
        SpaceRoleEnum sRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        ThrowUtils.throwIf(sRoleEnum == null, ErrorCode.NULL_ERROR, "要更新的角色不能为空");

        SpaceUser spaceUser = new SpaceUser();
        spaceUser.setId(id);
        spaceUser.setSpaceRole(sRoleEnum.getValue());

        boolean update = this.updateById(spaceUser);
        ThrowUtils.throwIf(!update, ErrorCode.SYSTEM_ERROR, "更新成员角色失败");
        return true;
    }

    @Override
    public SpaceUser getSpaceUser(Long spaceId, Long userId) {
        // 校验参数非空
        ThrowUtils.throwIf(spaceId == null || userId == null, ErrorCode.NULL_ERROR);

        // 从空间成员表中查询特定成员
        return this.lambdaQuery().eq(SpaceUser::getSpaceId, spaceId).eq(SpaceUser::getUserId, userId).one();
    }

    @Override
    public SpaceUser getSpaceUser(GetSpaceUserRequest getSpaceUserRequest) {
        // 校验参数
        ThrowUtils.throwIf(getSpaceUserRequest == null, ErrorCode.NULL_ERROR);
        Long spaceId = getSpaceUserRequest.getSpaceId();
        Long userId = getSpaceUserRequest.getUserId();
        return getSpaceUser(spaceId, userId);
    }

    @Override
    public List<SpaceUser> getAllSpaceUser(Long spaceId) {
        ThrowUtils.throwIf(spaceId == null, ErrorCode.NULL_ERROR);

        return this.lambdaQuery().eq(SpaceUser::getSpaceId, spaceId).list();
    }

    @Override
    public List<SpaceUser> getAllSpaceUser(GetAllSpaceUserRequest getAllSpaceUserRequest) {
        // 校验参数
        ThrowUtils.throwIf(getAllSpaceUserRequest == null, ErrorCode.NULL_ERROR);
        Long spaceId = getAllSpaceUserRequest.getSpaceId();
        return getAllSpaceUser(spaceId);
    }

    @Override
    public List<SpaceUser> querySpaceUserWithCondition(QuerySpaceUserRequest querySpaceUserRequest) {
        // 校验请求参数
        ThrowUtils.throwIf(querySpaceUserRequest == null, ErrorCode.NULL_ERROR);
        // 构造查询wrapper
        QueryWrapper<SpaceUser> queryWrapper = getQueryWrapper(querySpaceUserRequest);
        // 查询数据库
        // 返回结果
        return this.list(queryWrapper);
    }

    @Override
    public SpaceUserVO objToVo(SpaceUser spaceUser) {
        SpaceUserVO  spaceUserVO = new SpaceUserVO();
        BeanUtil.copyProperties(spaceUser, spaceUserVO);

        Long spaceId = spaceUserVO.getSpaceId();
        Long userId = spaceUserVO.getUserId();

        Space space = spaceService.getById(spaceId);
        User user = userService.getById(userId);

        spaceUserVO.setSpaceVO(SpaceVO.objToVO(space));
        spaceUserVO.setUserVO(UserVO.objToVO(user));

        return spaceUserVO;
    }

    @Override
    public QueryWrapper<SpaceUser> getQueryWrapper(QuerySpaceUserRequest querySpaceUserRequest) {
        QueryWrapper<SpaceUser> qw = new QueryWrapper<>();

        Long id = querySpaceUserRequest.getId();
        Long spaceId = querySpaceUserRequest.getSpaceId();
        Long userId = querySpaceUserRequest.getUserId();
        String spaceRole = querySpaceUserRequest.getSpaceRole();
        Date createTime = querySpaceUserRequest.getCreateTime();

        qw.eq(id != null, "id", id);
        qw.eq(spaceId != null && spaceId > 0, "spaceId", spaceId);
        qw.eq(userId != null && userId > 0, "userId", userId);
        SpaceRoleEnum sRoleEnum = SpaceRoleEnum.getEnumByValue(spaceRole);
        qw.eq(sRoleEnum != null, "spaceRole", sRoleEnum.getValue());

        // 创建时间在createTime之后
        qw.ge(createTime != null, "createTime", createTime);

        return qw;
    }
}




