package com.xinzeyijia.houselocks.ui.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.xinzeyijia.houselocks.R
import com.xinzeyijia.houselocks.model.bean.DataBean
import com.xinzeyijia.houselocks.utils.date_util.TimeUtils
import com.xinzeyijia.houselocks.utils.date_util.TimeUtils.DEFAULT_SDF
import com.xinzeyijia.houselocks.utils.date_util.TimeUtils.HOUR_SDF_D

class OrderItemAdapter : BaseQuickAdapter<DataBean, BaseViewHolder>(R.layout.order_home_item) {

    override fun convert(helper: BaseViewHolder?, item: DataBean?) {
        item!!.apply {
            helper!!.apply {
                addOnClickListener(R.id.no_id_card)
                addOnClickListener(R.id.yes_id_card)
                setText(
                    R.id.tv_time_no,
                    "日期：${TimeUtils.string2String(
                        item.startTime1,
                        DEFAULT_SDF,
                        HOUR_SDF_D
                    ) + "至" + TimeUtils.string2String(item.endTime1, DEFAULT_SDF, HOUR_SDF_D)}"
                )
                setText(R.id.tv_room_no, "房间号：$room_number")
                setText(R.id.tv_order_no, "订单号：$orderNo")
                val imgLock = getView<ImageView>(R.id.img_lock)
                val tv_room_state = getView<TextView>(R.id.tv_room_state)
                val no_id_card = getView<TextView>(R.id.no_id_card)

                if (isbangble == "1") {
                    imgLock.visibility = View.VISIBLE
                } else {
                    imgLock.visibility = View.GONE
                }
                if (is_check == "2" && attestation == "1") {
                    no_id_card.background = mContext.getDrawable(R.drawable.select_blue_round2)
                    no_id_card.text = "去开门"
                    no_id_card.isClickable = true
                } else {
                    no_id_card.background = mContext.getDrawable(R.drawable.select_blue_round2)
                    no_id_card.text = "去认证"
                    no_id_card.isClickable = true
                }
                if (is_check == "2" && attestation == "1" && isbangble == "0") {
                    no_id_card.background = mContext.getDrawable(R.drawable.round_bg_hui4)
                    no_id_card.text = "已入住"
                    no_id_card.isClickable = false
                }
                if (isSmartLock == "1") {
                    tv_room_state.text = "长租"
                } else {
                    tv_room_state.text = "短租"
                }
            }
        }

    }


}
