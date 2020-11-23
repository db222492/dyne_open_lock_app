package com.xinzeyijia.houselocks.model.http

object Urls {
    const val JWT_URL = "http://apitest.weixinlock.com/"
    const val BASE_URL3 = "https://aip.baidubce.com/" //本地环境2
    var BASE_URL2 = "https://www.lndyne.cn/ylxb/prod/api/" //线上
    const val BASE_URL = "https://www.xinzeyijia.com/ylxtest/api/v1/" //线下
    const val BASE_URL4 = "https://www.xinzeyijia.com/ylxtest/api/v1/" //线下
    const val FORM_URLENCODE_DATA = "application/x-www-form-urlencoded"
    const val VERSION_NAME = "wx/dyne/"
    const val getCode = VERSION_NAME + "lock/v1/iphoneSend"//手机验证码
    const val getOrderByIdcard = VERSION_NAME + "lock/v1/getOrderByIdcard"
    const val loadPicture = VERSION_NAME + "lock/v1/loadPicture" //上传身份证照片
    const val compareImg = VERSION_NAME + "lock/v1/compare" //图片对比
    const val openlock = VERSION_NAME + "lock/v1/dynebj/openlock" //获取开门信息
    const val openlog = VERSION_NAME + "lock/v1/dynebj/openlog" //开门回调
    const val mbsPsw = VERSION_NAME + "lock/v1/ble/mbsPwd" //摩比斯获取离线密码
    const val dynamicPasswd = VERSION_NAME + "lock/v1/wx/dynamicPasswd" //创维门锁的离线密码
    const val openHuanc = VERSION_NAME + "lock/v1/dynebj/openHuanc" //创维门锁的离线密码
    const val getBaseUrl = VERSION_NAME+"getBaseUrl" //获取域名版本

}