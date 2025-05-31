package com.tlm.storecollab.model.vo;
import java.util.Date;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjUtil;
import com.tlm.storecollab.model.entity.Task;
import lombok.Data;

@Data
public class TaskVO {

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 任务状态。
     */
    private String taskStatus;

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
     * 扩图成功后的访问url
     */
    private String imageUrl;

    public static TaskVO objToVO(Task task){
        if (task == null) {
            return null;
        }

        TaskVO taskVO = new TaskVO();
        BeanUtil.copyProperties(task, taskVO);

        return taskVO;
    }
}
