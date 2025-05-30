package com.tlm.storecollab.api.aliyunapi.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import cn.hutool.core.annotation.Alias;

/**
 * 任务创建响应对象。
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CreateOutPaintingTaskResponse {

    /**
     * 任务ID。
     */
    @Alias("task_id")
    private String taskId;

    /**
     * 任务状态。
     */
    @Alias("task_status")
    private String taskStatus;

    /**
     * 请求唯一标识。可用于请求明细溯源和问题排查。
     */
    @Alias("request_id")
    private String requestId;

    /**
     * 请求失败的错误码。请求成功时不会返回此参数，详情请参见错误信息。
     */
    private String code;

    /**
     * 请求失败的详细信息。请求成功时不会返回此参数，详情请参见错误信息。
     */
    private String message;
}