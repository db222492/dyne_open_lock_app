package com.xinzeyijia.houselocks.model.http.apiservice

import com.xinzeyijia.houselocks.base.BaseBean
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.base.PicBean
import com.xinzeyijia.houselocks.model.http.Urls
import io.reactivex.Observable
import okhttp3.MultipartBody
import retrofit2.http.*

/**
 * author : dibo
 * e-mail : db20206@163.com
 * date   : 2020/10/2213:38
 * desc   :
 * version: 1.0
 */
interface HomeApiService {

    /**
     * 首页精选
     */
    @POST(Urls.getCode)
    fun getCode(@Body map: Any): Observable<HomeData>

    /**
     * 首页精选
     */
    @POST(Urls.getOrderByIdcard)
    fun getOrderByIdcard(@Body map: Any): Observable<HomeData>

    //上传图片
    @Multipart
    @POST(Urls.loadPicture)
    fun loadPicture(
        @Part part: MultipartBody.Part,
        @QueryMap map: HashMap<String, String>
    ): Observable<PicBean>

    //上传图片
    @Multipart
    @POST(Urls.openlock)
    fun openlock(
        @Part part: MultipartBody.Part,
        @QueryMap map: HashMap<String, String>
    ): Observable<HomeData>

    //补录身份证和人脸对比的接口
    @POST(Urls.compareImg)
    fun compareImg(@Body map:Any): Observable<HomeData>

    //补录身份证和人脸对比的接口
    @POST(Urls.openlog)
    fun openlog(@Body map: Any): Observable<HomeData>

    //获取临时密码
    @POST(Urls.mbsPsw)
    fun mbsPsw(@Body map: HashMap<String, Any>): Observable<HomeData>

    //获取临时密码
    @POST(Urls.dynamicPasswd)
    fun dynamicPasswd(@Body map: HashMap<String, Any>): Observable<HomeData>

    //获取门锁信息缓存
    @POST(Urls.openHuanc)
    fun openHuanc(@Body map: HashMap<String, Any>): Observable<HomeData>

    /**
     * 获取请求的地址的版本
     * @param map Map<String?, Any?>? 参数
     * @return Observable<BaseBean?>?
     */
    @POST(Urls.getBaseUrl)
    fun getBaseUrl(@Body map: Map<String, Any?>): Observable<HomeData>

}