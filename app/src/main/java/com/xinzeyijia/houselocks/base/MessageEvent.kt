package com.xinzeyijia.houselocks.base

/**
 * <事件类型>
 * Created by admin on 2018/2/26.
</事件类型> */
class MessageEvent {
    var type: String
    var msg: String? = null

    constructor(type: String) {
        this.type = type
    }

    constructor(type: String, msg: String?) {
        this.type = type
        this.msg = msg
    }

}