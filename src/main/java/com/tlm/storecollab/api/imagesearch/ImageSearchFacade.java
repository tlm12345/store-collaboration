package com.tlm.storecollab.api.imagesearch;

import com.tlm.storecollab.api.imagesearch.model.ImageSearchResult;
import com.tlm.storecollab.api.imagesearch.sub.GetFirstUrlApi;
import com.tlm.storecollab.api.imagesearch.sub.GetImagePageUrlApi;
import com.tlm.storecollab.api.imagesearch.sub.GetSimImageInfoApi;

import java.util.List;

/**
 * 以图搜图
 */
public class ImageSearchFacade {

    public static List<ImageSearchResult> getImageSearchResult(String imageUrl)
    {
        String simPageUrl = GetImagePageUrlApi.getImagePageUrl(imageUrl);
        String firstUrl = GetFirstUrlApi.getFirstUrl(simPageUrl);
        List<ImageSearchResult> simImageInfo = GetSimImageInfoApi.getSimImageInfo(firstUrl);
        return simImageInfo;
    }
}
