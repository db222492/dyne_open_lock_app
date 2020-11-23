package com.xinzeyijia.houselocks.base

import com.hazz.kotlinmvp.base.IBaseView
import com.hazz.kotlinmvp.base.IPresenter
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable



/**
 * Created by xuhao on 2017/11/16.
 *
 */
open class BasePresenter<V : IBaseView> : IPresenter<V> {

    var v: V? = null
        private set

    private var cd = CompositeDisposable()


    override fun attachView(v: V) {
        this.v = v
    }

    override fun detachView() {
        v = null

         //保证activity结束时取消所有正在执行的订阅
        if (!cd.isDisposed) {
            cd.clear()
        }

    }

    private val isViewAttached: Boolean
        get() = v != null

    fun checkViewAttached() {
        if (!isViewAttached) throw MvpViewNotAttachedException()
    }

    fun addSubscription(disposable: Disposable) {
        cd.add(disposable)
    }

    private class MvpViewNotAttachedException internal constructor() : RuntimeException("Please call IPresenter.attachView(IBaseView) before" + " requesting data to the IPresenter")


}