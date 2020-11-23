package com.hazz.kotlinmvp.ui.fragment

import android.os.Bundle
import android.view.View
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.base.BaseFragment

/**
 * Created by xuhao on 2017/11/9.
 * 我的
 */
class MineFragment : BaseFragment(),View.OnClickListener {


    private var mTitle:String? =null


    companion object {
        fun getInstance(title:String): MineFragment {
            val fragment = MineFragment()
            val bundle = Bundle()
            fragment.arguments = bundle
            fragment.mTitle = title
            return fragment
        }
    }


    override fun getLayoutId(): Int= R.layout.fragment_home

    override fun initView() {


    }

    override fun lazyLoad() {

    }

    override fun showError(msg: String, errorCode: Int) {

    }

    override fun startLogin() {
        TODO("Not yet implemented")
    }

    override fun showLoading() {
        TODO("Not yet implemented")
    }

    override fun dismissLoading() {
        TODO("Not yet implemented")
    }

    override fun showMessage(message: String) {
        TODO("Not yet implemented")
    }


    override fun onClick(v: View?) {
        when{

        }
    }





}