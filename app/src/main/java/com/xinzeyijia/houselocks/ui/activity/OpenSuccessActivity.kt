package com.xinzeyijia.houselocks.ui.activity

import android.view.View
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.BaseActivity
import com.xinzeyijia.houselocks.base.Common
import kotlinx.android.synthetic.main.activity_open_lock_success.*
import kotlinx.android.synthetic.main.rlt_toolbar.*

/**
 * 开门成功
 */
class OpenSuccessActivity : BaseActivity() {

    override fun onDestroy() {
        super.onDestroy()
    }


    override fun showError(msg: String, errorCode: Int) {
    }

    override fun showLoading() {
    }

    override fun dismissLoading() {
    }

    override fun click(view: View) {
        super.click(view)
        when (view.id) {
            R.id.btn_cancel -> {
                finish()
            }
            R.id.back -> {
                finish()
            }
        }
    }

    override fun layoutId(): Int = R.layout.activity_open_lock_success

    override fun initData() {
        val isHaveLock = intent.getStringExtra(Common.IS_HAVE_LOCK)
        when (isHaveLock) {
            "1" -> {//无锁
                tv_hint.text = "您已入住成功"
            }
            "2" -> {//有锁
                tv_hint.text = "您已开门成功"
            }
        }
    }

    override fun initView() {
        tv_mycard.text = "一路向北"
    }


}