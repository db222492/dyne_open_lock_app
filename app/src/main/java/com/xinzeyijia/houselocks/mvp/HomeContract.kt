package com.xinzeyijia.houselocks.mvp

import com.xinzeyijia.houselocks.base.BasePresenter
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.base.PicBean
import com.hazz.kotlinmvp.base.IBaseView
import java.io.File
import java.util.HashMap

/**
 * Created by xuhao on 2017/11/8.
 * 契约类
 */

interface HomeContract {

    interface View : IBaseView {

        /**
         * 设置请求的数据
         */
        fun setHomeData(type: String, b: HomeData)

        /**
         * 设置第一次请求的数据
         */
        fun setHomeData(type: String, b: PicBean)

        /**
         * 显示错误信息
         */
        fun showError(msg: String, errorCode: Int)

        /**
         * 启动登录界面
         */
        fun startLogin()


    }

    abstract class Presenter : BasePresenter<View>() {
        /**
         * 获取验证码
         */
        abstract fun getCode(phone: String)
        /**
         * 订单列表
         */
        abstract fun orderList(IdCard: String, page: Int)
        /**
         * 上传人脸照片
         */
        abstract fun loadPicture(compressPath: String, idCard: String)
        /**
         * 认证对比
         */
        abstract fun compareImg(
            id: String,
            phone: String,
            roomNo: String,
            orderId: String,
            identity: String,
            idCardImg: String
        )
        /**
         * 上传人脸头像
         */
        abstract fun uploadFile(idCard: String, file: File, phone: String, id: Int?, roomId: String?)
        /**
         * 根据核验结果获取锁信息
         */
        abstract fun openLock(
            idCard: String,
            lockMac: String,
            lockType: String,
            phone: String,
            open_type: String
        )

        /**
         * 获取摩比斯的单次密码
         */
        abstract fun mbsPsw(params: HashMap<String, Any>)

        /**
         * 获取创维的离线密码
         */
        abstract fun dynamicPasswd(identity: String, phone: String, mac: String)

        /**
         * 根据订单id，房间id ，身份证号，请求锁详情
         */
        abstract fun openHuanc(identity: String, orderId: Int, roomNo: String)

    }
}
