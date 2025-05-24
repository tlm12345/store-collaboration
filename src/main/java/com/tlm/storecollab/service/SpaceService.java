package com.tlm.storecollab.service;

import com.tlm.storecollab.model.dto.space.SpaceCreateRequest;
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

}
