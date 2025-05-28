package com.tlm.storecollab.model.dto.picture;


import com.tlm.storecollab.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
public class PictureQueryRequest extends PageRequest implements Serializable{

    /**
     * 关键字搜索（管理员用）, 对名称和描述进行关键在搜索
     */
    private String searchText;

    /**
     * 图片名称
     */
    private String name;

    /**
     * 简介
     */
    private String introduction;

    /**
     * 分类
     */
    private String category;

    /**
     * 标签（ 数组 ）
     */
    private List<String> tags;

    /**
     * 图片体积下限
     */
    private Long minPicSize;
    /**
     * 图片体积上限
     */
    private Long maxPicSize;

    /**
     * 图片宽度
     */
    private Integer picWidth;

    /**
     * 图片高度
     */
    private Integer picHeight;

    /**
     * 图片宽高比例
     */
    private Double picScale;

    /**
     * 图片格式
     */
    private String picFormat;

    /**
     * 图片色调
     */
    private String picAve;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间查找(上界)
     */
    private Date startEditTime;

    /**
     * 编辑时间查找(下界)
     */
    private Date endEditTime;

    /**
     * 是否查看自己私人空间的图片
     */
    private Boolean queryPrivateSpace;
    /**
     * 私人空间id
     */
    private Long spaceId;
}
