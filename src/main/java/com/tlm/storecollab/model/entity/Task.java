package com.tlm.storecollab.model.entity;

import com.baomidou.mybatisplus.annotation.*;

import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 任务
 * @TableName task
 */
@TableName(value ="task")
@Data
public class Task implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 请求唯一标识。可用于请求明细溯源和问题排查。
     */
    private String requestId;

    /**
     * 任务状态。
     */
    private String taskStatus;

    /**
     * 图片结果 url
     */
    private String imageUrl;

    /**
     * 请求失败的错误码。请求成功时不会返回此参数
     */
    private String code;

    /**
     * 请求失败的消息。请求成功时不会返回此参数
     */
    private String message;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 编辑时间
     */
    private Date editTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer isDelete;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}