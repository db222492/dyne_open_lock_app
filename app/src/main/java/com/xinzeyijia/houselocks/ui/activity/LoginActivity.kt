package com.xinzeyijia.houselocks.ui.activity

import android.view.KeyEvent
import android.view.View
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.BaseActivity
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.base.HomeData
import com.xinzeyijia.houselocks.model.http.Urls
import com.xinzeyijia.houselocks.mvp.HomeContract
import com.xinzeyijia.houselocks.utils.CountdownUtil
import com.xinzeyijia.houselocks.utils.MMKVUtils
import com.xinzeyijia.houselocks.utils.RegexUtil
import kotlinx.android.synthetic.main.activity_login.*

/**
 * 登录界面
 *
 */
class LoginActivity : BaseActivity(), HomeContract.View {
    private var codeValue: String = ""
    private var code: String = ""
    private var countdownUtil: CountdownUtil? = null
    private var firstTime: Long = 0

    override fun layoutId(): Int = R.layout.activity_login
    override fun setHomeData(type: String, b: HomeData) {
        when (type) {
            Urls.getCode -> {
                if (b.status == 200) {
                    codeValue = b.data.value
                    countdownUtil = p.initTimeDown(tv_send_code)!!//倒计时
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (countdownUtil != null) {
            countdownUtil!!.stop()
            countdownUtil = null
        }
    }

    override fun click(view: View) {
        super.click(view)
        when (view.id) {
            R.id.tv_landing -> {//登录按钮的监听
                code = et_code.text.toString().trim()
                if (code.isNotEmpty()) {
                    if (code == codeValue) {
                        startActivity(HomeActivity::class.java)
                        finish()
                    } else {
                        makeToast("输入验证码有误")
                    }
                } else {
                    makeToast("验证码不能为空")
                }
            }
            R.id.tv_send_code -> {//发送验证码
                val idCard = et_id_card.text.toString().trim()
                val phone = et_phone.text.toString().trim()
                when {
                    idCard.isEmpty() -> {
                        makeToast("身份证号不能为空")
                        return
                    }
                    !RegexUtil.isIDCard18(idCard) -> {
                        makeToast("身份证号格式有误")
                        return
                    }
                    phone.isEmpty() -> {
                        makeToast("手机号不能为空")
                        return
                    }
                    !RegexUtil.isMobileExact(phone) -> {
                        makeToast("手机号格式有误")
                        return
                    }
                }
                MMKVUtils.put(Common.ID_CARD, idCard)
                MMKVUtils.put(Common.PHONE, phone)
                p.getCode(phone)
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


    override fun initData() {
        /**
         * 判断如果获取的身份证号不为空，就直接进入登录界面
         */
        if (MMKVUtils.getString(Common.ID_CARD, "").toString().isNotEmpty()) {
            startActivity(HomeActivity::class.java)
            finish()
        }
    }

    override fun initView() {

    }


    /**
     * 第二种办法
     * * @param keyCode
     * * @param event
     * * @return
     */
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.action == KeyEvent.ACTION_DOWN) {
            val secondTime = System.currentTimeMillis()
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