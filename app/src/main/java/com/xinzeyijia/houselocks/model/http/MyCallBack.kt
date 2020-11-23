package com.xinzeyijia.houselocks.model.http

import com.xinzeyijia.houselocks.utils.LogUtils.loge
import com.xinzeyijia.houselocks.utils.ToastUtil
import com.vise.xsnow.http.callback.ACallback

open class  MyCallBack<T> : ACallback<T>() {
    override fun onSuccess(data: T?) {  }

    override fun onFail(errCode: Int, errMsg: String?) {
        when (errCode) {
            504 -> {
                ToastUtil.show("网络已断开,请检查网络!")
            }
            502 -> {
                ToastUtil.show("正在发版,请联系后台!")
            }
            else -> {
                ToastUtil.show("请联系后台!$errCode-->$errMsg")
                loge("请联系后台!$errCode-->$errMsg")
            }
        }
    }

}