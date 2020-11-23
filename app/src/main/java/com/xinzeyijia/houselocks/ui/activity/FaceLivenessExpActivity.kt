package com.xinzeyijia.houselocks.ui.activity

import android.os.Bundle
import android.view.View
import com.baidu.idl.face.platform.FaceStatusNewEnum
import com.baidu.idl.face.platform.model.ImageInfo
import com.baidu.idl.face.platform.ui.FaceLivenessActivity
import com.baidu.idl.face.platform.ui.utils.IntentUtils
import com.gyf.immersionbar.ImmersionBar
import com.xinzeyijia.houselocks.App.Companion.addDestroyActivity
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.model.bean.BusBean
import com.xinzeyijia.houselocks.utils.TimeoutDialog
import com.xinzeyijia.houselocks.utils.TimeoutDialog.OnTimeoutDialogClickListener
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * 活体检测界面
 */
class FaceLivenessExpActivity : FaceLivenessActivity(), OnTimeoutDialogClickListener {
    private var mTimeoutDialog: TimeoutDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        fitsLayoutOverlap()
        super.onCreate(savedInstanceState)
        // 添加至销毁列表
        addDestroyActivity(
            this@FaceLivenessExpActivity,
            "FaceLivenessExpActivity"
        )
    }

    /**
     * 初始化沉浸式状态栏
     */
    private fun fitsLayoutOverlap() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .navigationBarDarkIcon(false)
            .statusBarDarkFont(true).init()
    }

    override fun onLivenessCompletion(
        status: FaceStatusNewEnum,
        message: String,
        base64ImageCropMap: HashMap<String, ImageInfo>,
        base64ImageSrcMap: HashMap<String, ImageInfo>,
        currentLivenessCount: Int
    ) {
        super.onLivenessCompletion(
            status,
            message,
            base64ImageCropMap,
            base64ImageSrcMap,
            currentLivenessCount
        )
        if (status == FaceStatusNewEnum.OK && mIsCompletion) {
            // showMessageDialog("活体检测", "检测成功");
            IntentUtils.getInstance().bitmap = mBmpStr
            //活体检测成功把图片传回上个界面
            EventBus.getDefault().postSticky(BusBean(Common.FACE_IMG, mBmpStr))
            finish()
        } else if (status == FaceStatusNewEnum.DetectRemindCodeTimeout) {
            if (mViewBg != null) {
                mViewBg.visibility = View.VISIBLE
            }
            showMessageDialog()
        }
    }

    /**
     * 活体
     */
    private fun showMessageDialog() {
        mTimeoutDialog = TimeoutDialog(this)
        mTimeoutDialog!!.setDialogListener(this)
        mTimeoutDialog!!.setCanceledOnTouchOutside(false)
        mTimeoutDialog!!.setCancelable(false)
        mTimeoutDialog!!.show()
        onPause()
    }

    override fun finish() {
        super.finish()
    }

    override fun onRecollect() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog!!.dismiss()
        }
        if (mViewBg != null) {
            mViewBg.visibility = View.GONE
        }
        onResume()
    }

    override fun onReturn() {
        if (mTimeoutDialog != null) {
            mTimeoutDialog!!.dismiss()
        }
        finish()
    }
}