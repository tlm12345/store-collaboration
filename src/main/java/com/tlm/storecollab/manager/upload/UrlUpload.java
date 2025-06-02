package com.tlm.storecollab.manager.upload;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpStatus;
import cn.hutool.http.HttpUtil;
import cn.hutool.http.Method;
import com.tlm.storecollab.common.ErrorCode;
import com.tlm.storecollab.common.ThrowUtils;
import com.tlm.storecollab.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class UrlUpload extends PictureUploadTemplate{
    @Override
    public String validPicture(Object inputSource) {
        String url = (String) inputSource;
        // 判断URL是否为空
        ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.NULL_ERROR, "URL为空");

        // 判断URL是否符合规范，是否为指定协议
        try {
            new URL(url);
        }catch (Exception e){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "不符合URL规范");
        }
        boolean isURL = StrUtil.startWith(url , "http") || StrUtil.startWith(url , "https");
        ThrowUtils.throwIf(!isURL, ErrorCode.PARAMS_ERROR, "只支持http或https协议");

        // 判断URL对应的网络资源是否是图片
        // 发送HEAD请求

        HttpResponse response = null;
        try {
            response = HttpUtil.createRequest(Method.HEAD, url).execute();
            // 判断请求是否成功
            int status = response.getStatus();
            if (status != HttpStatus.HTTP_OK){
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "网络资源获取失败");
            }
            String contentType = response.header("Content-Type");
            System.out.println("content-type is : " + contentType);

            // 判断是否是图片
            List<String> allowedContentTypes = Arrays.asList("image/jpeg", "image/png", "image/jpg", "image/webp");
            ThrowUtils.throwIf(!allowedContentTypes.contains(contentType.toLowerCase()), ErrorCode.PARAMS_ERROR, "只支持图片");

            String length = response.header("Content-Length");
            long lenSize = Long.parseLong(length);
            // 如果是图片，获取其内容大小，不超过2MB就通过校验
            ThrowUtils.throwIf(lenSize > 2 * 1024 * 1024, ErrorCode.FILE_TOO_BIG_ERROR, "文件太大超过上限");

            // 返回文件类型后缀
            return contentType.toLowerCase().substring(6);

        } catch (NumberFormatException e) {
            log.error("upload By url error: {}", e.getMessage());
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "文件大小格式错误或者url目标资源服务器未返回文件大小信息");
        } catch(Exception e){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "其他错误");

        } finally {
            if (response != null) {
                response.close();
            }

        }
    }

    @Override
    public String getOriginalName(Object inputSource, String ext) {
        String url = (String) inputSource;
        // 为了增加支持的url范围，对于url直接使用随机生成名称
        String mainName = RandomUtil.randomString(9);

        // 这里的png是默认值
        return mainName + "." + ext;
    }

    @Override
    public void downloadFile(Object inputSource, File file) {
        String url = (String) inputSource;
        try {
            HttpUtil.downloadFile(url, file);
        }catch(Exception e){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载URL指定的图片失败");
        }
    }
}
