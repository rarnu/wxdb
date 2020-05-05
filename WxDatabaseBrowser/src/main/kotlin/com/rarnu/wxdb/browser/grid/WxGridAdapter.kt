package com.rarnu.wxdb.browser.grid

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.rarnu.android.BaseAdapter
import com.rarnu.android.dip2px
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
                baseLayout.addView(TextView(context).apply {
                    layoutParams = LinearLayout.LayoutParams(75.dip2px(), LinearLayout.LayoutParams.MATCH_PARENT).apply {
                        rightMargin = 1
                    }
                    gravity = Gravity.START or Gravity.CENTER_VERTICAL
                    ellipsize = TextUtils.TruncateAt.END
                    setSingleLine(true)
                    maxLines = 1
                    setPadding(2.dip2px(), 0, 2.dip2px(), 0)
                    setBackgroundColor(if (position == 0) Color.LTGRAY else Color.WHITE)
                    paint.isFakeBoldText = position == 0
                    text = if (list[i].isBlob) "[BLOB]" else list[i].str
                    isClickable = true
                    setOnClickListener {
                        if (position > 0) {
                            listener?.onWxGridClick(position, i, list[i])
                        }
                    }
                })
            }
        }
    }
}