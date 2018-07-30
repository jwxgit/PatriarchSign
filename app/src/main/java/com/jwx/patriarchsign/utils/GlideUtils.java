package com.jwx.patriarchsign.utils;

import android.widget.ImageView;

import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;

/**
 * 描述：Glide图片加载
 * DiskCacheStrategy.NONE :不缓存图片
 * DiskCacheStrategy.SOURCE :缓存图片源文件
 * DiskCacheStrategy.RESULT:缓存修改过的图片
 * DiskCacheStrategy.ALL:缓存所有的图片，默认
 * 使用signature()实现自定义cacheKey：
 *
 * @author wmm
 * @github https://github.com/blindmonk
 * @time 2016/10/29
 */
public class GlideUtils {


    //加载本地图片
    public static void loadImage(int resourceId, ImageView container) {
        Glide.with(UIUtil.getContext()).load(resourceId).dontAnimate().into(container);
    }

    //加载图片
    public static void loadImage(String url, ImageView container, boolean centerCrop, int placeHolderSrcId, int errorSrcId) {
        DrawableTypeRequest<String> request = Glide.with(UIUtil.getContext()).load(url);
        DrawableRequestBuilder<String> requestBuilder;
        if (centerCrop) {
            requestBuilder = request.centerCrop();
            requestBuilder.placeholder(placeHolderSrcId).error(errorSrcId).crossFade().into(container);
            requestBuilder.into(container);
        } else {
            request.placeholder(placeHolderSrcId).error(errorSrcId).crossFade().into(container);
            request.into(container);
        }
//        else
//            requestBuilder = request.fitCenter();
//        requestBuilder.placeholder(placeHolderSrcId).error(errorSrcId).crossFade().into(container);
//        requestBuilder.into(container);
    }
}

