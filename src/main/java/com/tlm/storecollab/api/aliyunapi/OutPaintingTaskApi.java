package com.tlm.storecollab.api.aliyunapi;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskRequest;
import com.tlm.storecollab.api.aliyunapi.model.CreateOutPaintingTaskResponse;
import com.tlm.storecollab.api.aliyunapi.model.QueryOutPaintingTaskResponse;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 图像外绘任务API。
 */
@Slf4j
@Component
public class OutPaintingTaskApi {
    @Value("${aLiYun.secret}")
    private String apiKey;

    private static final String CREATE_OUT_PAINTING_TASK_API = "https://dashscope.aliyuncs.com/api/v1/services/aigc/image2image/out-painting";

    /**
     * 查询结果API， %s需要用task_id来替换
     */
    private static final String QUERY_OUT_PAINTING_TASK_API = "https://dashscope.aliyuncs.com/api/v1/tasks/%s";


    /**
     * 创建图像外绘任务。
     * 
     * @param request 请求参数
     * @return 创建任务的响应结果
     */
    public CreateOutPaintingTaskResponse createOutPaintingTask(CreateOutPaintingTaskRequest request) {
        try {
            String body = request.toJson();
            // 发送POST请求并获取响应
            HttpResponse response1 = HttpRequest.post(CREATE_OUT_PAINTING_TASK_API)
                    .header("Authorization", "Bearer " + this.apiKey)
                    .header("X-DashScope-Async", "enable")
                    .header("Content-Type", "application/json")
                    .body(body)
                    .execute();
            String response = (String) response1.body();
            log.info("响应体如下: {}", response);
            JSONObject jsonObj = JSONUtil.parseObj(response);

            // 解析JSON响应
            return JSONUtil.toBean((String) jsonObj.getObj("output"), CreateOutPaintingTaskResponse.class);

        } catch (Exception e) {
            throw new RuntimeException("创建图像外绘任务失败: " + e.getMessage(), e);
        }
    }
    
    /**
     * 查询图像外绘任务状态。
     * 
     * @param taskId 任务ID
     * @return 任务状态的响应结果
     */
    public QueryOutPaintingTaskResponse queryOutPaintingTask(String taskId) {
        if (StrUtil.isBlank(taskId)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "任务 id 不能为空");
        }
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(String.format(QUERY_OUT_PAINTING_TASK_API, taskId));
            httpGet.addHeader("Authorization", "Bearer " + apiKey);
            CloseableHttpResponse response = httpClient.execute(httpGet);
            String responseBody = EntityUtils.toString(response.getEntity(), "UTF-8");
            log.info("查询结果如下: {}", responseBody);
            if (response.getStatusLine().getStatusCode() != 200) {
                log.error("请求异常：{}", responseBody);
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "获取任务失败");
            }
            return JSONUtil.toBean(responseBody, QueryOutPaintingTaskResponse.class);
        } catch (IOException e) {
            log.error("获取图片 task 信息发生错误", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "系统错误，请稍后再试");
        }
    }

}