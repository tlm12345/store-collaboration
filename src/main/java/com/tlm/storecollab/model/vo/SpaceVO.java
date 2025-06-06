package com.tlm.storecollab.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.tlm.storecollab.model.entity.Space;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class SpaceVO {
    /**
     * id
     */
    private Long id;

    /**
     * 空间名称
     */
    private String spaceName;

    /**
     * 空间等级
     */
    private Integer spaceLevel;

    /**
     * 空间类别
     */
    private Integer spaceType;

    /**
     * 空间已使用容量
     */
    private Long spaceSizeUsed;

    /**
     * 空间最大容量
     */
    private Long spaceMaxSize;

    /**
     * 当前空间下已有图片数量
     */
    private Long spaceTotalCount;

    /**
     * 当前空间下最大图片数量
     */
    private Long spaceMaxCount;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建用户信息
     */
    private UserVO userVO;

    /**
     * 权限列表
     */
    private List<String> permissionList = new ArrayList<>();

    public static SpaceVO objToVO(Space space){
        if (space == null) return null;

        SpaceVO spaceVO = new SpaceVO();
        BeanUtil.copyProperties(space, spaceVO);
        return spaceVO;
    }

    public static Space VOToObj(SpaceVO spaceVO){
        if (spaceVO == null) return null;
        Space space = new Space();
        BeanUtil.copyProperties(spaceVO, space);
        return space;
    }
}
