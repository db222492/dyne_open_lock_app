package com.xinzeyijia.houselocks.model.ilock

import com.clj.fastble.data.BleDevice
import com.xinzeyijia.houselocks.model.bean.LockBean
import com.xinzeyijia.houselocks.model.bean.WslLockBean
import com.wunu.smartlink.sdk.model.CmdRspData

abstract class ALockManger : ILockManger {
    override fun lockControlResult(cmd: CmdRspData, data: Any?) {}
    override fun connectState(connectState: String, msg: String, progress: Int) {}
    override fun jwtOpenLockSuccess() = Unit
    override fun jwtNorify(loginType: String) = Unit
    override fun lockBindSuccess() = Unit
    override fun lockBindFiled() = Unit
    override fun jwtOpenLockFiled() = Unit
    override fun deviceVerify(lockBean: LockBean) = Unit
    override fun receiveData(lockBean: LockBean) = Unit
    override fun skyNotify_26(psw: String) = Unit
    override fun skyNotify_B1(hexData: String) = Unit
    override fun skyNotify_18(data: String) = Unit
    override fun skyNotify_11(lockId: String, lockVersion: String) = Unit
    override fun skyNotify_20(msg: String) {}
    override fun skyNotify_05() = Unit
    override fun isTextNotify(openLockNum: Int) {}
    override fun scanState(
        scanState: String,
        success: Boolean,
        device: BleDevice?,
        scanResultList: List<BleDevice>?,
        msg: String,
        progress: Int
    ) = Unit

    override fun updateLockUI(lockModel: WslLockBean) {}
    override fun notifyListener(data: ByteArray) {
    }

    override fun skyNotify_91(hexCmd: String, data: String) {
    }
}