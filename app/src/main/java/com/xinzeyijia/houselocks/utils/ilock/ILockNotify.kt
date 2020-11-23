package com.xinzeyijia.houselocks.model.ilock

import com.xinzeyijia.houselocks.model.bean.WslLockBean
import com.wunu.smartlink.sdk.model.CmdRspData

interface ILockNotify {
    fun skyNotify_B1(
        hexData: String
    )
    fun skyNotify_20(msg: String)
    fun skyNotify_05()

    fun skyNotify_18(data: String)
    fun skyNotify_11(lockId: String, lockVersion: String)
    fun skyNotify_26(psw: String)
    fun updateLockUI(
        lockModel: WslLockBean
    )

    fun lockControlResult(cmd: CmdRspData, data: Any?)
    fun notifyListener(data: ByteArray)
    fun skyNotify_91(hexCmd: String, data: String)
}