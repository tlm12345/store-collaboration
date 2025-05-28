package com.tlm.storecollab.api.imagesearch.sub;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;

/**
 * 获取搜狗以图搜图的接口
 */
@Deprecated
public class GetSouGouInterfaceApi {

    public static String getSouGouInterfaceUrl(String imageUrl) {
        String url = "https://ris.sogou.com/risapi/pc/sim?query=https://img02.sogoucdn.com/v2/thumb/retype_exclude_gif/ext/auto?appid=122&url=%s&start=24&plevel=-1";
        url = String.format(url, imageUrl);
        HttpResponse response = HttpRequest.get(url).execute();
        int status = response.getStatus();
        System.out.println(status);
        String body = response.body();
        System.out.println(body);

        return "ok";
    }

    public static void main(String[] args) {
        String imageUrl = "https://www.codefather.cn/logo.png";
        String res = getSouGouInterfaceUrl(imageUrl);
        return;
    }
}
