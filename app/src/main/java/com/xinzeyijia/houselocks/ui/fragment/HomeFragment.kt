package com.hazz.kotlinmvp.ui.fragment

import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.mvp.HomePresenter
import com.xinzeyijia.houselocks.base.BaseFragment

@Suppress("DEPRECATION")
/**
 * Created by xuhao on 2017/11/8.
 * 首页精选
 */

class HomeFragment : BaseFragment() {


    private val mPresenter by lazy { HomePresenter() }

    private var mTitle: String? = null

    private var num: Int = 1


    override fun getLayoutId(): Int = R.layout.fragment_home


    /**
     * 初始化 ViewI
     */
    override fun initView() {
        mPresenter.attachView(this)


    }
    override fun lazyLoad() {
    }



    /**
     * 显示 Loading （下拉刷新的时候不需要显示 Loading）
     */
    override fun showLoading() {
//        if (!isRefresh) {
//            isRefresh = false
//            mLayoutStatusView?.showLoading()
//        }
    }

    /**
     * 隐藏 Loading
     */
    override fun dismissLoading() {
//        mRefreshLayout.finishRefresh()
    }

    override fun showMessage(message: String) {

    }

    /**
     * 显示错误信息
     */
    override fun showError(msg: String, errorCode: Int) {
//        showToast(msg)
//        if (errorCode == ErrorStatus.NETWORK_ERROR) {
//            mLayoutStatusView?.showNoNetwork()
//        } else {
//            mLayoutStatusView?.showError()
//        }
    }

    override fun startLogin() {
        TODO("Not yet implemented")
    }


    override fun onDestroy() {
        super.onDestroy()
        mPresenter.detachView()
    }



}
