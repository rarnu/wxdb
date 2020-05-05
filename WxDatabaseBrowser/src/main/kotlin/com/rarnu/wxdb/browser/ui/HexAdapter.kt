package com.rarnu.wxdb.browser.ui

import android.content.Context
import android.view.View
import com.rarnu.android.BaseAdapter
import com.rarnu.wxdb.browser.R
import kotlinx.android.synthetic.main.item_hex.view.*
import kotlin.math.abs

class HexAdapter(ctx: Context, list: MutableList<Byte>) : BaseAdapter<Byte, HexAdapter.HexHolder>(ctx, list) {

    override fun fillHolder(baseVew: View, holder: HexHolder, item: Byte, position: Int) {
        var b = abs(item.toInt()).toString(16)
        if (b.length == 1) {
            b = "0$b"
        }
        if (item < 0) {
            b = "-$b"
        }
        holder.tvHex.text = b
    }

    override fun getAdapterLayout() = R.layout.item_hex

    override fun getValueText(item: Byte) = ""

    override fun newHolder(baseView: View) = HexHolder(baseView)

    inner class HexHolder(v: View) {
        internal val tvHex = v.tvHex
    }

}