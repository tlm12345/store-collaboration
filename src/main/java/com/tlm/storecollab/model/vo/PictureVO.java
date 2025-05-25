package com.tlm.storecollab.model.vo;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.tlm.storecollab.model.entity.Picture;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 图片视图
 */
@Data
public class PictureVO implements Serializable {
    private static final long serialVersionUID = 808922040726111804L;
    /**
     * id
     */
    private Long id;

    /**
     * 图片 url
     */
    private String url;

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
     * 标签（JSON 数组）
     */
    private String tags;

    /**
     * 图片体积
     */
    private Long picSize;

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
     * 创建用户 id
     */
    private Long userId;

    /**
     * 空间id
     */
    private Long spaceId;

    /**
     * 用户视图
     */
    private UserVO userVO;

    /**
     * 创建时间
     */
    private Date createTime;

    public static PictureVO objToVo(Picture picture) {
        PictureVO res = new PictureVO();
        BeanUtil.copyProperties(picture, res);
        return res;
    }
}
