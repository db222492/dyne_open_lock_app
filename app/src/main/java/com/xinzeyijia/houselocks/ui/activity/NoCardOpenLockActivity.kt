package com.xinzeyijia.houselocks.ui.activity

import android.content.Intent
import android.text.TextUtils
import android.view.View
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.BaseActivity
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.base.PicBean
import com.xinzeyijia.houselocks.model.http.Urls
import com.xinzeyijia.houselocks.model.http.Urls.compareImg
import com.xinzeyijia.houselocks.utils.*
import com.google.gson.internal.LinkedTreeMap
import com.luck.picture.lib.PictureSelector
import com.luck.picture.lib.config.PictureConfig
import com.luck.picture.lib.config.PictureMimeType
import com.luck.picture.lib.entity.LocalMedia
import kotlinx.android.synthetic.main.activity_psw_opendoor.*
import kotlinx.android.synthetic.main.rlt_toolbar.*

/**
 * 无证开锁
 */
class NoCardOpenLockActivity() : BaseActivity() {
    private var phone: String = ""
    private var idcardId: String = ""
    private var idCard: String = ""
    private var orderId: String = ""
    private var roomId: String = ""
    private var base64: String = ""
    private var compressPath: String = ""



    override fun layoutId(): Int = R.layout.activity_psw_opendoor
    override fun initData() {
        tv_mycard.text = "有证核验"
        phone = MMKVUtils.getString(Common.PHONE, "")
        idCard = MMKVUtils.getString(Common.ID_CARD, "")
        orderId = intent.getIntExtra(Common.ORDER_ID, 0).toString()
        roomId = intent.getStringExtra(Common.ROOM_ID).toString()
    }

    override fun initView() {
        PWUtil.getInstace().makeDialog(this, false, "正在上传信息请稍候...")

    }


    override fun onDestroy() {
        super.onDestroy()
        PWUtil.getInstace().destoryDialog()
    }


    override fun click(view: View) {
        super.click(view)
        when (view.id) {
            R.id.back -> {
                finish()
            }
            R.id.img_idcard -> {//身份证点击按钮
                openCameraPicture(PictureConfig.CHOOSE_REQUEST)
            }
            R.id.img_face -> {//人脸头像点击按钮
                openCameraPicture(PictureConfig.REQUEST_CAMERA)
            }
            R.id.tv_sure -> {//确认对比上传信息的按钮
                PWUtil.getInstace().makeDialog(this, false, "正在上传信息")
                    .showDialog()
                p.compareImg(idcardId, phone, roomId, orderId, idCard, base64)

            }

        }
    }

    /**
     * 图片选择器初始化调用
     */
    private fun openCameraPicture(
        chooseRequest: Int
    ) {
        PictureSelector.create(this)
            .openGallery(1)//全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
            .selectionMode(PictureConfig.SINGLE)// 多选 or 单选 PictureConfig.MULTIPLE or PictureConfig.SINGLE
            .previewImage(true)// 是否可预览图片 true or false
            .isCamera(true)// 是否显示拍照按钮 true or false
            .imageFormat(PictureMimeType.PNG_Q)// 拍照保存图片格式后缀,默认jpeg
//            .enableCrop(true)// 是否裁剪 true or false
            .freeStyleCropEnabled(true)// 裁剪框是否可拖拽 true or false
            .cropCompressQuality(40)//质量压缩
            .withAspectRatio(2, 3)
            .rotateEnabled(true) // 裁剪是否可旋转图片 true or false
            .scaleEnabled(true)// 裁剪是否可放大缩小图片 true or false
            .compress(true)
            .showCropFrame(true)// 是否显示裁剪矩形边框 圆形裁剪时建议设为false   true or false
            .showCropGrid(true)// 是否显示裁剪矩形网格 圆形裁剪时建议设为false    true or false
            .glideOverride(280, 280)
            .isDragFrame(true)// 是否可拖动裁剪框(固定)
            .loadCacheResourcesCallback(GlideCacheEngine.createCacheEngine())
            .imageEngine(GlideEngine.createGlideEngine())
            .minimumCompressSize(300)
            .forResult(chooseRequest)//结果回调onActivityResult code
    }

    override fun setHomeData(type: String, b: PicBean) {
        super.setHomeData(type, b)
        when (type) {
            Urls.loadPicture -> {
                if (b.status == 200) {//上传人脸照片的回调
                    if (b.data is Double) {
                        idcardId = (b.data as Double).toInt().toString()
                    } else {
                        idcardId = (b.data as LinkedTreeMap<*, *>)["id"].toString()
                    }
                }
                PWUtil.getInstace().disDialog()
            }
        }
    }

    /**
     * 图片选择器的回调
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val images: List<LocalMedia>
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                PictureConfig.CHOOSE_REQUEST -> {
                    // 图片选择结果回调
                    images = PictureSelector.obtainMultipleResult(data)
                    if (!TextUtils.isEmpty(images[0].realPath)) {
                        compressPath = images[0].realPath
                    }
                    ImageUtil.getInstance()
                        .getRoundRadiusImg(this, compressPath, img_idcard, 0, 0, 12, false)
                    base64 = ImageUtil.imageToBase64(compressPath)

                }
                PictureConfig.REQUEST_CAMERA -> {
                    // 图片选择结果回调
                    images = PictureSelector.obtainMultipleResult(data)
                    if (!TextUtils.isEmpty(images[0].realPath)) {
                        compressPath = images[0].realPath
                    }
                    ImageUtil.getInstance()
                        .getRoundRadiusImg(this, compressPath, img_face, 0, 0, 12, false)
                    PWUtil.getInstace().showDialog()
                    p.loadPicture(compressPath, idCard)

                }
            }// 例如 LocalMedia 里面返回三种path
            // 1.media.getPath(); 为原图path
            // 2.media.getCutPath();为裁剪后path，需判断media.isCut();是否为true
            // 3.media.getCompressPath();为压缩后path，需判断media.isCompressed();是否为true
            // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
        }
    }

    /**
     * 网络请求的回调
     */
    override fun setHomeData(type: String, b: HomeData) {
        when (type) {
            compareImg -> {
                PWUtil.getInstace().disDialog()
                if (b.status == 200) {
                    makeToast("入住成功")
                    val data = b.data
                    if (data.noLock == "1") {//判断是否有锁，如果无所就直接进入入住成功的界面，如果有锁就进入开锁界面
                        val intent = Intent(this, OpenSuccessActivity::class.java)
                        intent.putExtra(Common.IS_HAVE_LOCK, "1")
                        startActivity(intent)
                        finish()
                    } else {

                    }

                }
            }
        }
    }

    override fun showError(msg: String, errorCode: Int) {
    }

    override fun showLoading() {
        mLayoutStatusView?.showLoading()
    }

    override fun dismissLoading() {
        mLayoutStatusView?.showContent()
    }

}
