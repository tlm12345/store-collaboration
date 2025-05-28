package com.tlm.storecollab.api.imagesearch.sub;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import java.util.HashMap;
import java.util.Map;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;

/**
 * 以图搜图，根据图片URL获取相似图片所在的页面URL(第一步)
 */
@Slf4j
public class GetImagePageUrlApi {

    public static String getImagePageUrl(String imageUrl) {
        String url = "https://graph.baidu.com/upload?uptime=";
        String upTime = String.valueOf(System.currentTimeMillis());
        url = url + upTime;
        try {
            // 创建表单数据
            Map<String, Object> formData = new HashMap<>();
            formData.put("image", imageUrl);
            formData.put("tn", "pc");
            formData.put("from", "pc");
            formData.put("image_source", "PC_UPLOAD_URL");
            formData.put("sdkParams", "{\"data\":\"42f081bb9822421c5a570f171120b7e904122beff7f6e6fb3458968f15f830c04cccfec4c0ae9055db660c1db9643eeb3b771bc30c45cb30a0ee9b2e6ad269af24427a25ad089bdea9a67c7a9aa23e32\",\"key_id\":\"23\",\"sign\":\"1caff6c1\"}");
            HttpResponse httpResponse = null;

            try {
                // 发送POST请求并获取响应
                httpResponse = HttpRequest.post(url)
                        .header("accept", "*/*")
                        .header("accept-encoding", "gzip, deflate, br, zstd")
                        .header("accept-language", "zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
                        .header("acs-token", "1748320012671_1748392912604_HJU+1S8+dt5+QaU3GpAVEqmYqVszdi9mH6H0m2ZQS5WblVL1JuwW4sMVr8Jd8z2hqZ+ElD8NFw4FmAvXhZ7+TRGeQ4MgRCIaZjYaOZ7mK7OVXRCAGI7Szz+yVKmvq/EUXJjzUcBAJozX/cNRFqNh3aYH5BHqmaI5jUCobz7dqUCyoGSF6zNCHho5EC6RujESg5VeBhS/FXEVRgrEegpZi7RjzEYZ/HlsRr+3ulN1ut4ViH+n/dnafo9swFdnaf96QvUU+tRlJIZWgNPb0KCtSzfEt0rAP4iRKnMPdNfxeQEYwzG1g2YHsVN7Nq4xvhKE9qeL5hkqnKUwdnWIIZWBWddLC7UcJhqZtbYdV6YDhOBY9moXRsyvRrC4F92GD7VVxIjOV73ZQIfikxT1v0uknil/IoLxFF4hophT8c5rMMl1gwmZl8aPevjAXcVh0Y+Z")
                        .header("cache-control", "no-cache")
                        .header("origin", "https://graph.baidu.com")
                        .header("pragma", "no-cache")
                        .header("referer", "https://graph.baidu.com/pcpage/index?tpl_from=pc")
                        .header("sec-ch-ua", "\"Chromium\";v=\"136\", \"Microsoft Edge\";v=\"136\", \"Not.A/Brand\";v=\"99\"")
                        .header("sec-ch-ua-mobile", "?0")
                        .header("sec-ch-ua-platform", "\"Windows\"")
                        .header("sec-fetch-dest", "empty")
                        .header("sec-fetch-mode", "cors")
                        .header("sec-fetch-site", "same-origin")
                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/136.0.0.0 Safari/537.36 Edg/136.0.0.0")
                        .header("x-requested-with", "XMLHttpRequest")
                        .form(formData)
                        .execute();
            } catch (Exception e) {
                throw new BusinessException(ErrorCode.OPERATION_ERROR, "请求图像的相似页面地址失败!");
            }finally {
                if (httpResponse != null){
                    httpResponse.close();
                }
            }

            // 获取响应状态码
            int status = httpResponse.getStatus();
            // 获取响应体
            String body = httpResponse.body();
            if (HttpStatus.HTTP_OK != status){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "网络资源获取失败");
            }
            if (StrUtil.isBlank(body)){
                throw new BusinessException(ErrorCode.NULL_ERROR, "响应内容为空");

            }

            // 解析JSON响应
            JSONObject jsonObject = JSONUtil.parseObj(body);
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