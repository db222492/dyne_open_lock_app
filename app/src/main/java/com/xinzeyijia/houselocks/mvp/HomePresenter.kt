package com.xinzeyijia.houselocks.mvp

import android.annotation.SuppressLint
import android.widget.TextView
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.base.Common.SUCCESS_DATA
import com.xinzeyijia.houselocks.base.PicBean
import com.xinzeyijia.houselocks.model.http.Urls
import com.xinzeyijia.houselocks.utils.CountdownUtil
import com.xinzeyijia.houselocks.utils.LogUtils.loge
import com.hazz.kotlinmvp.net.exception.ExceptionHandle
import com.vise.xsnow.http.core.ApiTransformer
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

/**
 * Created by xuhao on 2017/11/8.
 * 首页精选的 Presenter
 * (数据是 Banner 数据和一页数据组合而成的 HomeBean,查看接口然后在分析就明白了)
 */

open class HomePresenter : HomeContract.Presenter() {


    private val homeModel: HomeModel by lazy {
        HomeModel
    }


    /**
     * 获取验证码
     */
    override fun getCode(phone: String) {
        checkViewAttached()
        v?.showLoading()
        val map = HashMap<String, Any>()
        map["phone"] = phone
        val disposable = homeModel.getHomeApiService().getCode(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    initDataType(homeBean, Urls.getCode, false, false, false, false)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 获取订单列表
     */
    override fun orderList(IdCard: String, page: Int) {
        checkViewAttached()
        v?.showLoading()
        val map = HashMap<String, Any>()
        map["IdCard"] = IdCard
        map["page"] = page
        map["limit"] = 100000
        val disposable = homeModel.getHomeApiService().getOrderByIdcard(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    initDataType(homeBean, Urls.getOrderByIdcard, false, false, false, false)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 上传身份证证照片
     */
    override fun loadPicture(compressPath: String, idCard: String) {
        checkViewAttached()
        v?.showLoading()
        val mfile = File(compressPath)
        val body = RequestBody.create(MediaType.parse("multipart/form-data"), mfile)//表单类型
        val part = MultipartBody.Part.createFormData("file", mfile.name, body) //添加图片数据，body创建的请求体
        val map = HashMap<String, String>()
        map["type"] = ""
        map["identity"] = idCard
        val disposable = homeModel.getHomeApiService().loadPicture(part, map)
            .compose(ApiTransformer.norTransformer<PicBean>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    v?.setHomeData(Urls.loadPicture, homeBean)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 人脸对比
     */
    override fun compareImg(
        id: String,
        phone: String,
        roomNo: String,
        orderId: String,
        identity: String,
        idCardImg: String
    ) {
        if (idCardImg.isEmpty()) {
            v?.showMessage("请先上传图片信息")
            return
        }
        val map = HashMap<String, String>()
        map["phone"] = phone//手机号
        map["roomNo"] = roomNo//房间号
        map["orderId"] = orderId//订单id
        map["identity"] = identity//身份证id
        map["idCardImg"] = idCardImg//身份证图片
        map["id"] = id//人脸图片id
        val disposable = homeModel.getHomeApiService().compareImg(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    initDataType(homeBean, Urls.compareImg, false, false, false, false)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 上传人脸图片
     */
    override fun uploadFile(idCard: String, file: File, phone: String, id: Int?, roomId: String?) {
        val body = RequestBody.create(MediaType.parse("multipart/form-data"), file)//表单类型
        val part = MultipartBody.Part.createFormData("upload", file.name, body) //添加图片数据，body创建的请求体
        val map = HashMap<String, String>()
        map["identity"] = idCard
//        map["upload"] = file
        map["phone"] = phone
        map["orderId"] = id!!.toString()
        map["roomNo"] = roomId!!.toString()
        val disposable = homeModel.getHomeApiService().openlock(part, map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    initDataType(homeBean, Urls.openlock, false, false, false, false)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 获取开锁信息
     */
    override fun openLock(
        idCard: String,
        lockMac: String,
        lockType: String,
        phone: String,
        open_type: String
    ) {
        val map = HashMap<String, String>()
        map["identity"] = idCard
        map["phone"] = phone
        map["mac"] = lockMac
        map["lock_version"] = lockType
        map["open_type"] = open_type
        val disposable = homeModel.getHomeApiService().openlog(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ homeBean ->
                v?.apply {
                    dismissLoading()
                    if (homeBean.status == 200) {//如果回调成功就给开锁类型赋值
                        homeBean.data.open_type = open_type
                    }
                    initDataType(homeBean, Urls.openlog, false, false, false, false)
                }
            }, { t ->
                v?.apply {
                    showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                }
            })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 摩比斯离线密码
     */
    override fun mbsPsw(params: HashMap<String, Any>) {
        val disposable = homeModel.getHomeApiService().mbsPsw(params)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ baseBean ->
                initDataType(baseBean, Urls.mbsPsw, false, true, false, false)
            },
                { t ->
                    v?.showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    /**
     * 创维门锁的离线密码
     */
    override fun dynamicPasswd(identity: String, phone: String, mac: String) {
        val map = HashMap<String, Any>()
        map["identity"] = identity
        map["phone"] = phone
        map["mac"] = mac
        val disposable = homeModel.getHomeApiService().dynamicPasswd(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ baseBean ->
                initDataType(baseBean, Urls.dynamicPasswd, false, true, false, false)
            },
                { t ->
                    v?.showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }

    override fun openHuanc(identity: String, orderId: Int, roomNo: String) {
        val map = HashMap<String, Any>()
        map["identity"] = identity
        map["orderId"] = orderId
        map["roomNo"] = roomNo
        val disposable = homeModel.getHomeApiService().openHuanc(map)
            .compose(ApiTransformer.norTransformer<HomeData>())
            .subscribe({ baseBean ->
                initDataType(baseBean, Urls.openHuanc, false, true, false, false)
            },
                { t ->
                    v?.showError(ExceptionHandle.handleException(t), ExceptionHandle.errorCode)
                })
        if (disposable != null) {
            addSubscription(disposable)
        }
    }



    private fun initDataType(
        HomeData: HomeData,
        typeData: String,
        is_show_message: Boolean,
        isFinish: Boolean,
        isSave: Boolean,
        is_null_data: Boolean
    ) {
        v?.setHomeData(typeData, HomeData)
        when {
            HomeData.status == SUCCESS_DATA -> {
                if (isSave) {
                }
                if (is_show_message) v?.showMessage(HomeData.message)
            }

            HomeData.status == Common.START_LOGIN -> {
                v?.startLogin()
            }
            HomeData.status == Common.LOGIN_USER_BANNED -> {
                v?.showMessage(HomeData.message)
            }
            HomeData.status == 40101 -> {
                v?.showMessage(HomeData.message)
            }
            HomeData.status == Common.FILED -> {
                v?.showMessage(HomeData.message)

            }
            HomeData.status == Common.SEVER_300 -> {
                v?.showMessage(HomeData.message)
            }

            HomeData.status == Common.NO_USER -> {
                v?.showMessage(HomeData.message)
            }
            HomeData.status == Common.SEVER_ERROR -> {
                v?.showMessage(HomeData.message)
                loge(typeData, HomeData.message)
            }
            HomeData.status == Common.NO_TOKEN -> {
                v?.showMessage(HomeData.message)
                v?.startLogin()
            }
            HomeData.status == Common.NO_TOKEN2 -> {
                v?.showMessage(HomeData.message)
                v?.startLogin()
            }
            HomeData.status == Common.LOGIN_USER_ANOTHOER_PLACE_ONLINE -> {
                v?.showMessage(HomeData.message)
                v?.startLogin()
            }
            HomeData.status == Common.EXCEPTION -> {
                v?.showMessage("请联系管理员！")
                loge(typeData, "请联系管理员！" + HomeData.message)
            }
            HomeData.status == Common.ERROR -> {
                v?.showMessage("请联系管理员！")
                loge(typeData, "请联系管理员！" + HomeData.message)
            }
            HomeData.status == -6 -> {//未查询到门锁id
                v?.showMessage("未查询到门锁id，请到修改管理员密码页，同步门锁id")
                loge(typeData, "请联系管理员！" + HomeData.status)
            }
            HomeData.status == Common.SEVER_STOP -> {
                v?.showMessage(HomeData.message)
                loge(typeData, HomeData.message)
            }
            HomeData.status == -96 -> {
                v?.showMessage(HomeData.message)
            }
            else -> {
//                v.showMessage(HomeData.message)
                loge(typeData, HomeData.message)
            }
        }
    }

    fun initTimeDown(tv_get_code: TextView): CountdownUtil? {
        val countdownUtil = CountdownUtil.newInstance()
        countdownUtil!!.totalTime(60000)
            .intervalTime(1000)
            .callback(object : CountdownUtil.OnCountdownListener {
                @SuppressLint("SetTextI18n")
                override fun onRemain(millisUntilFinished: Long) {
                    val time = millisUntilFinished / 1000
                    tv_get_code.text = "重新发送(" + time.toString() + "s)"
                    tv_get_code.isClickable = false
                }

                override fun onFinish() {
                    tv_get_code.text = "重新获取验证码"
                    tv_get_code.isClickable = true
                }
            }).start()
        return countdownUtil
    }
}
