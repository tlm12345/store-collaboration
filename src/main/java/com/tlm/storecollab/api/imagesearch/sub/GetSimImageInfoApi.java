package com.tlm.storecollab.api.imagesearch.sub;

import com.tlm.storecollab.api.imagesearch.model.ImageSearchResult;
import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 获取相似图片信息 (第三步)
 */
@Slf4j
public class GetSimImageInfoApi {

    public static List<ImageSearchResult> getSimImageInfo(String infoInterface) {
        try {
            // 发送GET请求并获取响应
            String response = HttpRequest.get(infoInterface)
                    .execute()
                    .body();


            // 解析JSON响应为对象列表
            JSONObject jsonObject = JSONUtil.parseObj(response);
            if (jsonObject.containsKey("data") && jsonObject.getJSONObject("data").containsKey("list")) {
                return JSONUtil.toList(jsonObject.getJSONObject("data").getJSONArray("list"), ImageSearchResult.class);
            }

            return null;

        } catch (Exception e) {
            log.error("GetSimImageInfoApi error::: {}", e.getMessage());
            return null;
        }
    }

    public static void main(String[] args) {
        String upImageUrl = "https://www.codefather.cn/logo.png";
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(upImageUrl);
        String firstUrl = GetFirstUrlApi.getFirstUrl(imagePageUrl);
        List<ImageSearchResult> simImageInfo = getSimImageInfo(firstUrl);
        for (ImageSearchResult imageSearchResult : simImageInfo) {
            System.out.println(imageSearchResult.getThumbUrl());
        }
    }
}