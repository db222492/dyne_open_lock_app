package com.xinzeyijia.houselocks.mvp

import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.model.http.Urls
import com.xinzeyijia.houselocks.model.http.apiservice.HomeApiService
import com.xinzeyijia.houselocks.utils.MMKVUtils
import com.vise.xsnow.http.ViseHttp
import com.xinzeyijia.houselocks.BuildConfig
import com.xinzeyijia.houselocks.model.http.interceptor.logging.LogInterceptor

/**
 * Created by xuhao on 2017/11/21.
 * desc: 首页精选 model
 */

object HomeModel {
    @JvmStatic
    fun getBASE_URL(): String? {
        return if (BuildConfig.DEBUG) {
//            Urls.BASE_URL + version + "/"
            Urls.BASE_URL2
        } else {
            Urls.BASE_URL2
        }
    }

    @JvmStatic
    open fun getBASE_URL4(): String? {
        return if (BuildConfig.DEBUG) {
            Urls.BASE_URL2
        } else {
            Urls.BASE_URL2
        }
    }
    @JvmStatic
    fun getHomeApiService(): HomeApiService {
        return ViseHttp.RETROFIT<Any>()
            .baseUrl(getBASE_URL4())
            .readTimeOut(9)
            .connectTimeOut(9)
            .interceptor(LogInterceptor())
//            .addHeader("Authorization", getHeaderValue())
            .create(HomeApiService::class.java)
    }
    @JvmStatic
    fun getHomeApiService3(): HomeApiService? {
        return ViseHttp.RETROFIT<Any>()
            .baseUrl(getBASE_URL4())
            .readTimeOut(9)
            .connectTimeOut(9)
            .interceptor(LogInterceptor())
//            .addHeader("Authorization", getHeaderValue())
            .create(HomeApiService::class.java)
    }

    private fun getHeaderValue(): String? {
        return MMKVUtils.getString(Common.TOKEN, "") as String?
    }

    fun getHomeApiService2(): HomeApiService? {
        return ViseHttp.RETROFIT<Any>()
            .baseUrl(getBASE_URL())
            .readTimeOut(60)
            .connectTimeOut(60) //配置日志拦截器
            .interceptor(LogInterceptor())
//            .addHeader("Authorization", getHeaderValue())
            .create(HomeApiService::class.java)
//        return RetrofitClient.getInstance().create(HomeApiService.class, getBASE_URL(), 5 * 60);
    }

    //RetrofitClient.getInstance().create(HomeApiService.class, JWT_URL)
    var JWT_SERVICE = ViseHttp.RETROFIT<Any>()
        .baseUrl(Urls.JWT_URL)
        .interceptor(LogInterceptor())
//        .addHeader("Authorization", getHeaderValue())
        .create(HomeApiService::class.java)

    //    public static final HomeApiService HOME_API_SERVICE3 = RetrofitClient2.getInstance().create(HomeApiService.class, BASE_URL3);
    val HOME_API_SERVICE3 = ViseHttp.RETROFIT<Any>()
        .baseUrl(Urls.BASE_URL3)
        .interceptor(LogInterceptor())
//        .addHeader("Authorization", getHeaderValue())
        .create(HomeApiService::class.java)
    val version: String
        get() = MMKVUtils.getString(Common.BASE_URL_VERSION, "/v1").toString()

    val getFile: String =
        getBASE_URL().toString() + com.xinzeyijia.houselocks.model.http.Urls.VERSION_NAME + "lock/v1/dynebj/getFile?id=" //图片拼接
}
