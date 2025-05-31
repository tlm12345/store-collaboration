package com.tlm.storecollab.api.aliyunapi.model;

import lombok.*;
import cn.hutool.core.annotation.Alias;

/**
 * 查询图像外绘任务响应对象。
 */
@Data
public class QueryOutPaintingTaskResponse {
    /**
     * 请求唯一标识。可用于请求明细溯源和问题排查。
     */
    @Alias("request_id")
    private String requestId;

    /**
     * 图像统计信息。
     */
    @Alias("usage")
    private Usage usage;

    @Alias("output")
    private Output output;


    /**
     * 任务统计信息。
     */
    @Data
    public class TaskMetrics {
        /**
         * 总的任务数。
         */
        private Integer total;

        /**
         * 成功的任务数。
         */
        private Integer succeeded;

        /**
         * 失败的任务数。
         */
        private Integer failed;
    }

    /**
     * 图像生成统计信息。
     */
    @Data
    public class Usage {
        /**
         * 模型生成图片的数量。
         */
        @Alias("image_count")
        private Integer imageCount;
    }

    @Data
    public class Output {
        /**
         * 任务ID。
         */
        @Alias("task_id")
        private String taskId;

        /**
         * 任务状态。
         *
         * 枚举值：
         * - PENDING：任务排队中
         * - RUNNING：任务处理中
         * - SUCCEEDED：任务执行成功
         * - FAILED：任务执行失败
         * - CANCELED：任务取消成功
         * - UNKNOWN：任务不存在或状态未知 n    */
        @Alias("task_status")
        private TaskStatusEnum taskStatus;

        /**
         * 任务结果统计信息。
         */
        @Alias("task_metrics")
        private TaskMetrics taskMetrics;

        /**
         * 任务提交时间，格式为ISO8601。
         */
        @Alias("submit_time")
        private String submitTime;

        /**
         * 任务完成时间，格式为ISO8601。
         */
        @Alias("end_time")
        private String endTime;

        /**
         * 输出图像URL地址。
         */
        @Alias("output_image_url")
        private String outputImageUrl;

        /**
         * 请求失败的错误码。请求成功时不会返回此参数。
         */
        private String code;

        /**
         * 请求失败的详细信息。请求成功时不会返回此参数。
         */
        private String message;
    }
}