package com.xinzeyijia.houselocks.ui.activity

import android.content.Intent
import android.util.Log
import android.view.KeyEvent
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.amap.api.location.AMapLocationListener
import com.baidu.idl.face.platform.FaceEnvironment
import com.baidu.idl.face.platform.FaceSDKManager
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.baidu.idl.face.platform.listener.IInitCallback
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.BaseActivity
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.model.bean.BusBean
import com.xinzeyijia.houselocks.model.bean.DataBean
import com.xinzeyijia.houselocks.model.http.Urls
import com.xinzeyijia.houselocks.ui.adapter.GlideImageLoader
import com.xinzeyijia.houselocks.ui.adapter.OrderItemAdapter
import com.xinzeyijia.houselocks.utils.*
import com.xinzeyijia.houselocks.utils.LogUtils.loge
import com.xinzeyijia.houselocks.utils.compression.FileUtil
import com.xinzeyijia.houselocks.utils.compression.Luban
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.check_idcard_item.view.*
import kotlinx.android.synthetic.main.recyclerview_item.*
import kotlinx.android.synthetic.main.rlt_toolbar.*
import java.io.File

/*

* 文 件 名：HomeActivity
* 描    述：展示订单列表
* 作    者：dibo
* 时    间：2020/11/2
* 版    权：

*/
class HomeActivity : BaseActivity() {

    private var saveLock: String = ""
    private var imgPath: String = ""
    private var longitude: Double = 0.0
    private var latitude: Double = 0.0
    private var phone: String = ""
    private var dataBean: DataBean? = null
    private var rows: MutableList<DataBean>? = null
    private var orderItemAdapter: OrderItemAdapter? = null
    private var idCard: String = ""
    private var page: Int = 1
    private var mIsInitSuccess: Boolean = false

    override fun onDestroy() {
        super.onDestroy()
        MMKVUtils.put(Common.OPEN_LOCK, "")//清除开锁记录
    }

    override fun layoutId(): Int = R.layout.activity_main

    override fun setHomeData(type: String, b: HomeData) {
        when (type) {
            Urls.getOrderByIdcard -> {
                try {
                    if (orderItemAdapter != null) {
                        if (b.data.currentPage == 1)
                            orderItemAdapter!!.data.clear()
                        rows = b.data.rows
                        orderItemAdapter!!.addData(rows!!)
                        page++

                        //设置网络请求后的占位图
                        DefIconUtil.instance.getNoDataIcon(
                            multiple_status_view!!,
                            orderItemAdapter!!,
                            R.layout.def_no_order_data
                        )//设置网络请求后的占位图
                    }
                } catch (e: Exception) {
                    loge(type, e.message)
                }
            }
            Urls.openlock -> {
                PWUtil.getInstace().disDialog()
                deleteImg(imgPath)//上传完信息，清除图片
                val data = b.data
                if (b.status == 200) {
                    startRequest()
                    if (data.noLock == "1") {
                        makeToast("核验成功")
                        val intent = Intent(this, OpenSuccessActivity::class.java)
                        intent.putExtra(Common.IS_HAVE_LOCK, "1")
                        startActivity(intent)
                    } else {
                        makeToast("核验成功")

                    }
                }
            }
            Urls.openHuanc -> {
                val data = b.data

            }
        }
    }

    private fun deleteImg(path: String) {
        try {
            FileUtil.getInstance().delete(path)
        } catch (e: Exception) {
            loge("图片清理失败")
            e.printStackTrace()
        }
    }

    override fun Event(b: BusBean) {
        super.Event(b)
        when (b.type) {
            Common.FACE_IMG -> {//人脸识别
                faceImgCallBack(b)
            }
            Common.FRESH_HOME_PAGE -> {//刷新请求
                startRequest()
            }
        }
    }

    private fun faceImgCallBack(b: BusBean) {
        PWUtil.getInstace().makeDialog(this, false, "信息正在上传请稍候...")
            .showDialog()
        val img64 = b.t as String
        imgPath = Path.HEAD_FILE_PATH + "face_img.jag"
        val file = File(imgPath)
        Luban.saveImage(imgPath, Luban.base64ToBitmap(img64), 100)
        p.uploadFile(
            idCard,
            file,
            phone,
            dataBean?.id,
            dataBean?.room_id
        )
    }

    //如果你需要考虑更好的体验，可以这么操作
    override fun onStart() {
        super.onStart()
        //开始轮播
        banner?.startAutoPlay()
    }

    override fun onStop() {
        super.onStop()
        //结束轮播
        banner?.stopAutoPlay()
    }

    override fun showError(msg: String, errorCode: Int) {

    }

    override fun showLoading() {
        mLayoutStatusView?.showLoading()
    }

    override fun dismissLoading() {
        mLayoutStatusView?.showContent()
    }

    override fun initData() {
        phone = MMKVUtils.getString(Common.PHONE, "")
        idCard = MMKVUtils.getString(Common.ID_CARD, "")

        val list = arrayListOf<String>()
        list.add("http://xinzetaoyuan.oss-cn-beijing.aliyuncs.com/21.png")
        list.add("http://xinzetaoyuan.oss-cn-beijing.aliyuncs.com/12.png")
        banner.setImageLoader(GlideImageLoader())
        banner.setOnBannerListener {
            if (it == 1) {
                PWUtil.getInstace().defPW(R.layout.def_popu_item3, this).build()
                    .addTitle("提示")
                    .addContent(
                        String.format(
                            getString(R.string.banner_hint),
                            phone.substring(phone.length - 4, phone.length),
                            idCard
                        )
                    )
                    .defListener()
            }
        }
        //设置图片集合
        banner.setImages(list)
        banner.start()
        initAdapter()//初始化适配器
        initRefresh()//初始化刷新
        initItemListener()//初始化item点击事件
        GaoDeUtils.getJuLi(mLocationListener)
    }

    private val mLocationListener =
        AMapLocationListener { aMapLocation ->
            if (aMapLocation != null) {
                if (aMapLocation.errorCode == 0) {
                    val locationType =
                        aMapLocation.locationType //获取当前定位结果来源，如网络定位结果，详见定位类型表
                    latitude = aMapLocation.latitude //获取纬度
                    longitude = aMapLocation.longitude //获取经度
                    val accuracy = aMapLocation.accuracy //获取精度信息
                    loge(
                        "定位",
                        "$locationType +$latitude+$longitude+$accuracy"
                    )

                } else {
                }
            }
        }

    private fun initLicense() {
        setFaceConfig()
        // 为了android和ios 区分授权，appId=appname_face_android ,其中appname为申请sdk时的应用名
        // 应用上下文
        // 申请License取得的APPID
        // assets目录下License文件名
        FaceSDKManager.getInstance().initialize(this, Common.licenseID,
            Common.licenseFileName, object : IInitCallback {
                override fun initSuccess() {
                    runOnUiThread {
                        Log.e("==", "初始化成功")
                        mIsInitSuccess = true
                    }
                }

                override fun initFailure(errCode: Int, errMsg: String) {
                    runOnUiThread {
                        Log.e("==", "初始化失败 = $errCode $errMsg")
                        mIsInitSuccess = false
                    }
                }
            })
    }

    /**
     * 参数配置方法
     */
    private fun setFaceConfig() {
        // 动作活体条目集合
        var livenessList = arrayListOf<LivenessTypeEnum>()
        livenessList.add(LivenessTypeEnum.Eye)
        livenessList.add(LivenessTypeEnum.HeadLeftOrRight)
//        livenessList.add(LivenessTypeEnum.HeadRight)
//        livenessList.add(LivenessTypeEnum.HeadLeft)
//        livenessList.add(LivenessTypeEnum.HeadUp)
//        livenessList.add(LivenessTypeEnum.HeadDown)
//        livenessList.add(LivenessTypeEnum.HeadLeftOrRight)

        val config = FaceSDKManager.getInstance().faceConfig
        // SDK初始化已经设置完默认参数（推荐参数），也可以根据实际需求进行数值调整
        // 设置可检测的最小人脸阈值
        config.minFaceSize = FaceEnvironment.VALUE_MIN_FACE_SIZE
        // 设置可检测到人脸的阈值
        config.notFaceValue = FaceEnvironment.VALUE_NOT_FACE_THRESHOLD
        // 设置模糊度阈值
        config.blurnessValue = FaceEnvironment.VALUE_BLURNESS
        // 设置光照阈值（范围0-255）
        config.brightnessValue = FaceEnvironment.VALUE_BRIGHTNESS
        // 设置遮挡阈值
        config.occlusionValue = FaceEnvironment.VALUE_OCCLUSION
        // 设置人脸姿态角阈值
        config.headPitchValue = FaceEnvironment.VALUE_HEAD_PITCH
        config.headYawValue = FaceEnvironment.VALUE_HEAD_YAW
        // 设置闭眼阈值
        config.eyeClosedValue = FaceEnvironment.VALUE_CLOSE_EYES
        // 设置图片缓存数量
        config.cacheImageNum = FaceEnvironment.VALUE_CACHE_IMAGE_NUM
        // 设置口罩判断开关以及口罩阈值
        config.isOpenMask = FaceEnvironment.VALUE_OPEN_MASK
        config.maskValue = FaceEnvironment.VALUE_MASK_THRESHOLD
        // 设置活体动作，通过设置list，LivenessTypeEunm.Eye, LivenessTypeEunm.Mouth,
        // LivenessTypeEunm.HeadUp, LivenessTypeEunm.HeadDown, LivenessTypeEunm.HeadLeft,
        // LivenessTypeEunm.HeadRight, LivenessTypeEunm.HeadLeftOrRight
        config.livenessTypeList = livenessList
        // 设置动作活体是否随机
        config.isLivenessRandom = true
        // 设置开启提示音
        config.isSound = true
        // 原图缩放系数
        config.scale = FaceEnvironment.VALUE_SCALE
        // 抠图高的设定，为了保证好的抠图效果，我们要求高宽比是4：3，所以会在内部进行计算，只需要传入高即可
        config.cropHeight = FaceEnvironment.VALUE_CROP_HEIGHT
        // 抠图人脸框与背景比例
        config.enlargeRatio = FaceEnvironment.VALUE_CROP_ENLARGERATIO
        // 加密类型，0：Base64加密，上传时image_sec传false；1：百度加密文件加密，上传时image_sec传true
        config.secType = FaceEnvironment.VALUE_SEC_TYPE
        FaceSDKManager.getInstance().faceConfig = config
    }

    private fun initItemListener() {

        orderItemAdapter!!.setOnItemChildClickListener { adapter, view, position ->
            dataBean = orderItemAdapter!!.data[position]
            if (longitude == 0.0) {
                makeToast("正在获取到您当前的位置，请稍后重试")
                return@setOnItemChildClickListener
            }
            val distance = LocationUtil.getDistance(
                dataBean!!.latitude, dataBean!!.longitude
                , latitude, longitude
            )
            loge("distance------------->$distance")
            if (distance <= 50) {
                if (dataBean!!.isbangble == "1") {
                    MMKVUtils.put(Common.ORDER_ID, dataBean!!.id)
                    MMKVUtils.put(Common.ROOM_ID, dataBean?.room_id)
                }
                if (dataBean!!.is_check == "2" && dataBean!!.attestation == "1" && dataBean!!.isbangble == "1") {//必须在有锁的时候存储订单号，请求锁缓存
                    p.openHuanc(idCard, dataBean!!.id, dataBean!!.room_id)
                    return@setOnItemChildClickListener
                }
                when (view.id) {
                    R.id.no_id_card -> {//无证核验
                        if (!mIsInitSuccess) {//判断初始化是否成功，如果不成功就再次初始化
                            initLicense()
                        } else {
                            val intent = Intent(this, FaceLivenessExpActivity::class.java)
                            intent.putExtra(Common.ORDER_ID, dataBean?.id)
                            intent.putExtra(Common.ROOM_ID, dataBean?.room_id)
                            startActivity(intent)
                        }
                    }
                    R.id.yes_id_card -> {//有证核验
                        NoCardOpenLockActivity::class.java
                        val intent = Intent(this, NoCardOpenLockActivity::class.java)
                        intent.putExtra(Common.ORDER_ID, dataBean?.id)
                        intent.putExtra(Common.ROOM_ID, dataBean?.room_id)
                        startActivity(intent)
                    }
                }
            } else {
                makeToast("请在门前开门")
            }


        }
    }


    private fun initRefresh() {
        refreshLayout.setOnRefreshListener { refreshLayout ->
            refreshLayout.layout.postDelayed({//下拉刷新
                DefIconUtil.instance.getNoNetIcon(
                    multiple_status_view!!,
                    orderItemAdapter!!,
                    R.layout.def_no_order_data
                )//初始化没有网的情况下的空试图
                refreshLayout.setEnableLoadMore(true)
                page = 1
                startRequest()
                refreshLayout.finishRefresh()
            }, 100)
        }


    }

    override fun initView() {
        tv_mycard.text = "一路向北"
        back.visibility = View.GONE
        initLicense()//初始化人脸核验的配置信息
    }

    private fun initAdapter() {
        rcw.layoutManager = LinearLayoutManager(this)
        orderItemAdapter = OrderItemAdapter()//订单列表适配器
        val inflate = View.inflate(this, R.layout.check_idcard_item, null)
        inflate.btn_check_idcard.setOnClickListener {//切换身份证的按钮，点击清空缓存的数据，然后进入登录界面
            MMKVUtils.getsMMKV().clearAll()
            startActivity(LoginActivity::class.java)
            finish()
        }
        orderItemAdapter?.addFooterView(inflate)
        rcw.adapter = orderItemAdapter
    }

    override fun startRequest() {
        super.startRequest()
        p.orderList(idCard, page)
    }
    private var firstTime: Long = 0//首次点击的时间
    /**
     * 第二种办法
     * * @param keyCode
     * * @param event
     * * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            val secondTime = System.currentTimeMillis()//获取当前点击的时间
            if (secondTime - firstTime > 2000) {
                makeToast(R.string.press_exit_again)
                firstTime = secondTime
                return true
            } else {
                finish()
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}