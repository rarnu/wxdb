package com.rarnu.wxdb.browser.grid

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.rarnu.kt.android.BaseAdapter
import com.rarnu.kt.android.dip2px
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.FieldData

class WxGridAdapter(ctx: Context, list: MutableList<List<FieldData>>) : BaseAdapter<List<FieldData>, WxGridAdapter.WxGridHolder>(ctx, list) {

    interface WxGridListener {
        fun onWxGridClick(row: Int, col: Int, data: FieldData)
    }

    var listener: WxGridListener? = null

    override fun fillHolder(baseVew: View, holder: WxGridHolder, item: List<FieldData>, position: Int) {
        holder.setCell(item, position)
    }

    override fun getAdapterLayout() = R.layout.item_cells

    override fun getValueText(item: List<FieldData>) = ""

    override fun newHolder(baseView: View) = WxGridHolder(baseView)

    inner class WxGridHolder(v: View) {
        private val baseLayout = v as LinearLayout
        fun setCell(list: List<FieldData>, position: Int) {
            baseLayout.removeAllViews()
            for (i in 0 until list.size) {
                val txt = TextView(context)
                val lp = LinearLayout.LayoutParams(75.dip2px(), LinearLayout.LayoutParams.MATCH_PARENT)
                lp.rightMargin = 1
                txt.layoutParams = lp
                txt.gravity = Gravity.START or Gravity.CENTER_VERTICAL
                txt.ellipsize = TextUtils.TruncateAt.END
                txt.setSingleLine(true)
                txt.maxLines = 1
                txt.setPadding(2.dip2px(), 0, 2.dip2px(), 0)
                txt.setBackgroundColor(if (position == 0) Color.LTGRAY else Color.WHITE)
                txt.paint.isFakeBoldText = position == 0
                txt.text = if (list[i].isBlob) "[BLOB]" else list[i].str
                txt.isClickable = true
                txt.setOnClickListener {
                    if (position > 0) {
                        listener?.onWxGridClick(position, i, list[i])
                    }
                }
                baseLayout.addView(txt)
            }
        }
    }
}