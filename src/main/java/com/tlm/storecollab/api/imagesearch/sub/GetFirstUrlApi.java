package com.tlm.storecollab.api.imagesearch.sub;

import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.exception.BusinessException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 获取相似图片的接口URL (第二步)
 * @author tlm + tongyi_Linma
 */
public class GetFirstUrlApi {
    
    public static String getFirstUrl(String simPageUrl) {
        try {
            // 获取网页HTML内容
            Document document = Jsoup.connect(simPageUrl).get();
            
            // 获取HTML内容字符串
            String htmlContent = document.outerHtml();
            
            // 正则表达式匹配firstUrl属性
            Pattern pattern = Pattern.compile("\"firstUrl\"\\s*:\\s*['\"']([^'\"']+)['\"]");
            Matcher matcher = pattern.matcher(htmlContent);
            
            if (matcher.find()) {
                String rawUrl = matcher.group(1);
                // 将转义后的字符串转换为正常的URL格式
                return rawUrl.replace("\\/", "/");
            }

            throw new BusinessException(ErrorCode.OPERATION_ERROR, "firstUrl not found");

        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    public static void main(String[] args) {
        String upImageUrl = "https://www.codefather.cn/logo.png";
        String imagePageUrl = GetImagePageUrlApi.getImagePageUrl(upImageUrl);
        String firstUrl = getFirstUrl(imagePageUrl);
        System.out.println(firstUrl);
    }
}