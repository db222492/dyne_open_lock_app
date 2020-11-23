package com.xinzeyijia.houselocks.model.bean

class DataBean {
    var value = ""
    var id = 0
    var longitude = 0.0
    var latitude = 0.0
    var room_id = ""
    var startTime1 = ""
    var endTime1 = ""
    var room_number = ""
    var orderNo = ""
    var isbangble = ""
    var is_check = ""//is_check 1 未入住  2 已入住
    var attestation = ""//1 已认证 0 未认证
    var isSmartLock = ""
    var blue_mac = ""
    var noLock = ""
    var mac = ""
    var open_type = ""
    var lock_version = ""
    var dyne_lock_name = ""
    var lockData = ""
    var lockId = ""
    var psw = ""
    var ble_key = ""
    var kjxId = ""
    var lockManagerId = ""
    var ble_uuid_service_id = ""
    var ble_uuid_receive_id = ""
    var lock_type = ""
    var lock_type_en = ""
    var blue_key = ""
    var ble_uuid_send_id = ""
    var currentPage = 0
    var rows: MutableList<DataBean> = ArrayList()
}
