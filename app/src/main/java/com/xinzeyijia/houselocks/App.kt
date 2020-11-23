package com.xinzeyijia.houselocks

import android.app.Activity
import android.app.Application
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.multidex.MultiDex
import com.baidu.idl.face.platform.LivenessTypeEnum
import com.clj.fastble.BleManager
import com.hazz.kotlinmvp.utils.DisplayManager
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.FormatStrategy
import com.orhanobut.logger.Logger
import com.orhanobut.logger.PrettyFormatStrategy
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.header.ClassicsHeader
import com.squareup.leakcanary.RefWatcher
import com.vise.log.ViseLog
import com.vise.log.inner.LogcatTree
import com.vise.xsnow.http.ViseHttp
import com.xinzeyijia.houselocks.BuildConfig.DEBUG
import com.xinzeyijia.houselocks.base.Common
import com.xinzeyijia.houselocks.utils.MMKVUtils
import com.xinzeyijia.houselocks.utils.ToastUtil
import me.jessyan.autosize.AutoSizeConfig
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.*
import javax.net.ssl.*
import kotlin.properties.Delegates

/**
 *    author : dibo
 *    e-mail : db20206@163.com
 *    date   : 2020/10/2210:51
 *    desc   :
 *    version: 1.0
 */
class App : Application() {
    private var refWatcher: RefWatcher? = null

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(this)
    }

    companion object {

        private val TAG = "MyApplication"

        @JvmStatic
        var context: Context by Delegates.notNull()
            private set

        fun getRefWatcher(context: Context): RefWatcher? {
            val myApplication = context.applicationContext as App
            return myApplication.refWatcher
        }

        @JvmStatic
        // 动作活体条目集合
        var livenessList = mutableListOf<LivenessTypeEnum>()

        // 活体随机开关
        var isLivenessRandom = false

        // 语音播报开关
        var isOpenSound = true

        // 活体检测开关
        var isActionLive = true

        private val destroyMap: MutableMap<String, Activity> =
            HashMap()

        /**
         * 添加到销毁队列
         * @param activity 要销毁的activity
         */
        @JvmStatic
        fun addDestroyActivity(
            activity: Activity,
            activityName: String
        ) {
            destroyMap[activityName] = activity
        }

        /**
         * 销毁指定Activity
         */
        fun destroyActivity(activityName: String?) {
            val keySet: Set<String> = destroyMap.keys
            for (key in keySet) {
                destroyMap[key]!!.finish()
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
//        refWatcher = setupLeakCanary()
        MMKVUtils.init(this)
        initConfig()
        DisplayManager.init(this)
        registerActivityLifecycleCallbacks(mActivityLifecycleCallbacks)
        initLog()
        initBle()
        initNet()
        initRefresh()
        ToastUtil.setBgColor(ContextCompat.getColor(this, R.color.hui3))
        AutoSizeConfig.getInstance().isCustomFragment = true
        handleSSLHandshake()
    }

    fun handleSSLHandshake() {
        try {
            val trustAllCerts =
                arrayOf<TrustManager>(object : X509TrustManager {
                    override fun getAcceptedIssuers(): Array<X509Certificate?> {
                        return arrayOfNulls(0)
                    }

                    override fun checkClientTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }

                    override fun checkServerTrusted(
                        certs: Array<X509Certificate>,
                        authType: String
                    ) {
                    }
                })
            val sc = SSLContext.getInstance("TLS")
            // trustAllCerts信任所有的证书
            sc.init(null, trustAllCerts, SecureRandom())
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.socketFactory)
            HttpsURLConnection.setDefaultHostnameVerifier { hostname: String?, session: SSLSession? -> true }
        } catch (ignored: Exception) {
        }
    }

    private fun initRefresh() {
        //设置全局的Header构建器

        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator { context: Context?, layout: RefreshLayout ->
            //            layout.setEnableAutoLoadMore(true);
            //            layout.setEnableOverScrollDrag(false);
            //            layout.setEnableOverScrollBounce(true);
            //            layout.setEnableLoadMoreWhenContentNotFull(true);
            //            layout.setEnableScrollContentWhenRefreshed(true);
            layout.setEnableLoadMoreWhenContentNotFull(false)
            //在刷新时候禁止操作内容视图
            layout.setDisableContentWhenLoading(true)
            layout.setDisableContentWhenRefresh(true)
            //            layout.setNoMoreData(true);
            layout.setPrimaryColorsId(R.color.white, R.color.hui3) //全局设置主题颜色
            ClassicsHeader(context) //.setTimeFormat(new DynamicTimeFormat("更新于 %s"));//指定为经典Header，默认是 贝塞尔雷达Header
        }
        //设置全局的Footer构建器
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator { context: Context?, layout: RefreshLayout? ->
            ClassicsFooter(
                context
            ).setDrawableSize(20f)
        }
    }

    private fun initBle() {
        BleManager.getInstance().init(this)
        BleManager.getInstance()
            .enableLog(true)
            .setReConnectCount(3, 5000)
            .setSplitWriteNum(20)
            .setConnectOverTime(15000)
            .setOperateTimeout(15000)
    }

    private fun initLog() {
        ViseLog.getLogConfig()
            .configAllowLog(true) //是否输出日志
            .configShowBorders(true) //是否排版显示
        ViseLog.plant(LogcatTree()) //添加打印日志信息到Logcat的树

    }

    private fun header(): Map<String, String>? {
        val header: MutableMap<String, String> =
            HashMap()
        header["Content-Type"] = "application/json"
        return header
    }

    private fun initNet() {
        ViseHttp.init(this)
//        ViseHttp.CONFIG().baseUrl(homeModel.getBASE_URL())
//            //配置连接超时时间，单位秒
//            .connectTimeout(30)
//            //请求header
//            .globalHeaders(header())
//            //配置日志拦截器
//            .interceptor(
//                HttpLogInterceptor()
//                    .setLevel(HttpLogInterceptor.Level.BODY)
//            );
    }

    /**
     * 初始化配置
     */
    private fun initConfig() {
        val formatStrategy: FormatStrategy = PrettyFormatStrategy.newBuilder()
            .showThreadInfo(false) //（可选）是否显示线程信息。 默认值为true
            .methodCount(0) // （可选）要显示的方法行数。 默认2
            .methodOffset(7) // （可选）设置调用堆栈的函数偏移值，0的话则从打印该Log的函数开始输出堆栈信息，默认是0
            //                .logStrategy( )  //（可选）更改要打印的日志策略。 默认LogCat
            .tag(Common.TAG) //（可选）每个日志的全局标记。 默认PRETTY_LOGGER（如上图）
            .build()

        Logger.addLogAdapter(object : AndroidLogAdapter(formatStrategy) {
            // 是否开启打印功能，返回true则打印，否则不打印
            override fun isLoggable(priority: Int, tag: String?): Boolean {
                return DEBUG
            }
        })
    }


    private val mActivityLifecycleCallbacks = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {
            Log.d(TAG, "onCreated: " + activity.componentName.className)
        }

        override fun onActivityStarted(activity: Activity) {
            Log.d(TAG, "onStart: " + activity.componentName.className)
        }

        override fun onActivityResumed(activity: Activity) {

        }

        override fun onActivityPaused(activity: Activity) {

        }

        override fun onActivityStopped(activity: Activity) {

        }

        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {

        }

        override fun onActivityDestroyed(activity: Activity) {
            Log.d(TAG, "onDestroy: " + activity.componentName.className)
        }
    }
}