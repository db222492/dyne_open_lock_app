package com.xinzeyijia.houselocks.model.ilock

import com.clj.fastble.data.BleDevice
import com.xinzeyijia.houselocks.model.bean.LockBean

interface ILockManger : ILockNotify {
    fun deviceVerify(lockBean: LockBean)
    fun receiveData(lockBean: LockBean)
    fun jwtNorify(loginType: String)
    fun lockBindSuccess()
    fun lockBindFiled()
    fun jwtOpenLockSuccess()
    fun jwtOpenLockFiled()
    fun isTextNotify(openLockNum: Int)
    /**
     * 蓝牙搜索状态
     */
    fun scanState(
        scanState: String,
        success: Boolean,
        device: BleDevice?,
        scanResultList: List<BleDevice>?,
        msg: String,
        progress: Int
    )

    /**
     * 蓝牙连接状态
     */
    fun connectState(
        connectState: String,
        msg: String,
        progress: Int
    )

}