package com.xinzeyijia.houselocks.utils;

import android.content.Context;

import com.luck.picture.lib.engine.CacheResourcesEngine;

import java.io.File;

/**
 * @author：luck
 * @date：2020-03-24 09:48
 * @describe：GlideCacheEngine
 */
public class GlideCacheEngine implements CacheResourcesEngine {

    @Override
    public String onCachePath(Context context, String url) {
        File cacheFile;
        cacheFile = ImageCacheUtils.getCacheFileTo4x(context, url);
        return cacheFile != null ? cacheFile.getAbsolutePath() : "";
    }

    private GlideCacheEngine() {
    }

    private static GlideCacheEngine instance;

    public static GlideCacheEngine createCacheEngine() {
        if (null == instance) {
            synchronized (GlideCacheEngine.class) {
                if (null == instance) {
                    instance = new GlideCacheEngine();
                }
            }
        }
        return instance;
    }
}
