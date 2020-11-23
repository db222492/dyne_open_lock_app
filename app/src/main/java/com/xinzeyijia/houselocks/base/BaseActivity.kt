package com.xinzeyijia.houselocks.base

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import com.classic.common.MultipleStatusView
import com.xinzeyijia.houselocks.App
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.model.bean.BusBean
import com.xinzeyijia.houselocks.mvp.HomeContract
import com.xinzeyijia.houselocks.mvp.HomePresenter
import com.xinzeyijia.houselocks.utils.ActivityManager
import com.xinzeyijia.houselocks.utils.LogUtils.loge
import com.xinzeyijia.houselocks.utils.MMKVUtils
import com.xinzeyijia.houselocks.utils.PWUtil
import com.xinzeyijia.houselocks.utils.ToastUtil
import com.xinzeyijia.houselocks.utils.permissionutil.PermissionListener
import com.xinzeyijia.houselocks.utils.permissionutil.PermissionUtil
import com.gyf.immersionbar.ImmersionBar
import com.ttlock.bl.sdk.api.TTLockClient
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode


/**
 * @author dibo
 * created: 2017/10/25
 * desc:BaseActivity基类
 */
abstract class BaseActivity : AppCompatActivity(),
    HomeContract.View {
    /**
     * 多种状态的 View 的切换
     */
    protected var mLayoutStatusView: MultipleStatusView? = null
    val p by lazy { HomePresenter() }

    init {
        p.attachView(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutId())
        if (!EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().register(this)
        initPermission()
        ActivityManager.getInstance().addActivity(this)
        fitsLayoutOverlap()
        initData()
        initView()
        startRequest()
        initListener()


    }

    /**
     * make sure Bluetooth is enabled.
     */
    open fun ensureBluetoothIsEnabled() {
        if (!TTLockClient.getDefault().isBLEEnabled(this)) {
            TTLockClient.getDefault().requestBleEnable(this)
        }
    }

    override fun showError(msg: String, errorCode: Int) {
        makeToast(msg)
        loge(Common.TAG, "$errorCode$msg")
    }

    override fun setHomeData(type: String, b: HomeData) {

    }

    override fun setHomeData(type: String, b: PicBean) {

    }

    private fun fitsLayoutOverlap() {
        ImmersionBar.with(this)
            .titleBar(R.id.toolbar)
            .navigationBarDarkIcon(false)
            .statusBarDarkFont(true).init()
    }

    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    open fun Event(b: BusBean) {
    }

    override fun showMessage(message: String) {
        makeToast(message)
    }

    open fun closeBlueTouch() {}

    open fun openBlueTouch() {}
    private fun initPermission() {
        //创建PermissionUtil对象，参数为继承自V4包的 FragmentActivity
        val permissionUtil = PermissionUtil(this)
        //调用requestPermissions
        val permissions = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,  //                    Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.BLUETOOTH,  //                    Manifest.permission.READ_PHONE_STATE,
            //                    Manifest.permission.SYSTEM_ALERT_WINDOW,
            //                    Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.CAMERA
        )
        permissionUtil.requestPermissions(permissions,
            object : PermissionListener {
                override fun onGranted() {
                    //所有权限都已经授权
                    val isFirst: Boolean =
                        MMKVUtils.getBoolean(Common.IS_FIRST, true)
                    if (isFirst) {
//                            makeToast("所有权限都已授权");
                        MMKVUtils.put(Common.IS_FIRST, false)
                    }
                }

                override fun onDenied(deniedPermission: List<String?>?) {
                    //Toast第一个被拒绝的权限
                    PWUtil.getInstace().showMsg(this@BaseActivity, "您有部分权限被拒绝，可能会导致部分功能不能使用")
                }

                override fun onShouldShowRationale(deniedPermission: List<String?>?) {
                    //Toast第一个勾选不在提示的权限
                    PWUtil.getInstace().showMsg(this@BaseActivity, "您有部分权限被禁止提示，如需打开请到应用管理页设置")
                }
            })
    }

    override fun startLogin() {
        ToastUtil.show("用户失效请重新登录！")
//        startActivity(Intent(this, LoginActivity::class.java))
//        finish()
    }

    /**
     * startActivity 方法简化
     */
    open fun startActivity(clazz: Class<out Activity>) {
        startActivity(Intent(this, clazz))
    }

    open fun makeToast(content: String) {
        ToastUtil.show(content)
    }

    open fun makeToast(content: Int) {
        ToastUtil.show(content, Toast.LENGTH_LONG)
    }

    private fun initListener() {
        mLayoutStatusView?.setOnClickListener(mRetryClickListener)
    }

    open val mRetryClickListener: View.OnClickListener = View.OnClickListener {
        startRequest()
    }

    open fun click(view: View) {
        onClick(view)
    }

    open fun alreadyClick() {}

    open fun onClick(view: View?) {}


    /**
     *  加载布局
     */
    abstract fun layoutId(): Int

    /**
     * 初始化数据
     */
    abstract fun initData()

    /**
     * 初始化 View
     */
    abstract fun initView()

    /**
     * 开始请求
     */
    open fun startRequest()  {}


    /**
     * 打卡软键盘
     */
    fun openKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN)
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    /**
     * 关闭软键盘
     */
    fun closeKeyBord(mEditText: EditText, mContext: Context) {
        val imm = mContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(mEditText.windowToken, 0)
    }


    override fun onDestroy() {
        super.onDestroy()
        App.getRefWatcher(this)?.watch(this)
        if (EventBus.getDefault().isRegistered(this))
            EventBus.getDefault().unregister(this)
        ActivityManager.getInstance().removeActivity(this)
    }


    /**
     * 重写要申请权限的Activity或者Fragment的onRequestPermissionsResult()方法，
     * 在里面调用EasyPermissions.onRequestPermissionsResult()，实现回调。
     *
     * @param requestCode  权限请求的识别码
     * @param permissions  申请的权限
     * @param grantResults 授权结果
     */
    override fun onRequestPermissionsResult(
        requestCode: Int,
        @NonNull permissions: Array<String>,
        @NonNull grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


}


