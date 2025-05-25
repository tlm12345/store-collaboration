package com.tlm.storecollab.service;

import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
import com.tlm.storecollab.model.dto.space.SpaceUpdateRequest;
import com.tlm.storecollab.model.entity.Picture;
import com.tlm.storecollab.model.entity.Space;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tlm.storecollab.model.entity.User;

/**
* @author 12483
* @description 针对表【space(空间)】的数据库操作Service
* @createDate 2025-05-24 22:22:35
*/
public interface SpaceService extends IService<Space> {

    /**
     * 为用户创建私人空间
     * @param spaceCreateRequest
     * @param loginUser
     * @return
     */
    Long createPrivateSpace(SpaceCreateRequest spaceCreateRequest, User loginUser);

    /**
     * 验证用户私人空间是否还有空闲空间
     * @param userId
     * @return
     */
    boolean validPrivateSpaceIsFree(Long userId);
    boolean validPrivateSpaceIsFree(Space space);


    /**
     * 根据图片大小释放空间容量相关信息(没有涉及图片存储的删除操作，仅修改空间表中的信息)
     * @param pic
     * @param space
     * @param isAdd 是添加图片还是删除图片
     */
    void updateSpaceCapacityInfo(Picture pic, Space space, boolean isAdd);

    /**
     * 校验更新空间的请求
     * @param spaceUpdateRequest
     */
    void validUpdateSpaceRequest(SpaceUpdateRequest spaceUpdateRequest);

}
