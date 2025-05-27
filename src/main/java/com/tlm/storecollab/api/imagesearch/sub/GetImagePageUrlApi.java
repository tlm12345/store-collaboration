package com.tlm.storecollab.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.Method;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 以图搜图，根据图片URL获取相似图片所在的页面URL
 */
@Slf4j
public class GetImagePageUrlApi {

    public static String getImagePageUrl(String imageUrl) {
        String url = "https://graph.baidu.com/upload?uptime=";
        String upTime = String.valueOf(System.currentTimeMillis());
        url = url + upTime;
        log.info("upload url is : {}", url);
        try {
            // Create form data map
            Map<String, Object> formData = new HashMap<>();
            formData.put("image", imageUrl);
            formData.put("tn", "pc");
            formData.put("from", "pc");
            formData.put("image_source", "PC_UPLOAD_URL");

            HttpResponse response = HttpRequest.post(url)
                    .form(formData)
                    .execute();
            int status = response.getStatus();
            if (HttpStatus.HTTP_OK != status){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "网络资源获取失败");
            }
            String body = response.body();
            if (StrUtil.isBlank(body)){
                throw new BusinessException(ErrorCode.NULL_ERROR, "响应内容为空");

            }

            // 解析JSON响应
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (jsonObject.containsKey("data")) {
                JSONObject dataObject = jsonObject.getJSONObject("data");
                if (dataObject != null && dataObject.containsKey("url")) {
                    return dataObject.getStr("url");
                }
            }
            throw new RuntimeException("Response JSON does not contain 'url' field");
        } catch (Exception e) {
            // 打印异常信息
            System.err.println(ExceptionUtil.stacktraceToString(e));
            // 返回错误信息
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String imageUrl = "https://www.codefather.cn/logo.png";
        String imagePageUrl = getImagePageUrl(imageUrl);
        log.info(imagePageUrl);

    }
}