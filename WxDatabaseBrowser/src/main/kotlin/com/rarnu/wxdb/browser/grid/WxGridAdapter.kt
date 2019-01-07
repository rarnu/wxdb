package com.rarnu.wxdb.browser.grid

import android.content.Context
import android.graphics.Color
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.LinearLayout
import android.widget.TextView
import com.rarnu.kt.android.dip2px
import com.rarnu.wxdb.browser.R

class WxGridAdapter(ctx: Context, lst: MutableList<List<String>>): BaseAdapter() {

    interface WxGridListener {
        fun onWxGridClick(row: Int, col: Int, isBlob: Boolean, text: String)
    }

    private val context = ctx
    private val list = lst
    var listener: WxGridListener? = null

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        var v = convertView
        if (v == null) {
            v = LayoutInflater.from(context).inflate(R.layout.item_cells, parent, false)
        }
        var holder = v?.tag as? WxGridHolder
        if (holder == null) {
            holder = WxGridHolder(v!!)
            v.tag = holder
        }
        val item = list[position]
        holder.setCell(item, position)
        return v!!
    }

    override fun getItem(position: Int) = list[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getCount() = list.size

    inner class WxGridHolder(v: View) {
        private val baseLayout = v as LinearLayout
        fun setCell(list: List<String>, position: Int) {
            baseLayout.removeAllViews()
            for (i in 0 until list.size) {
                val txt = TextView(context)
                val lp = LinearLayout.LayoutParams(75.dip2px(), LinearLayout.LayoutParams.MATCH_PARENT)
                lp.rightMargin = 1
                txt.layoutParams = lp
                txt.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
                txt.ellipsize = TextUtils.TruncateAt.END
                txt.setSingleLine(true)
                txt.maxLines = 1
                txt.setPadding(2.dip2px(), 0, 2.dip2px(), 0)
                txt.setBackgroundColor(if (position == 0) Color.LTGRAY else Color.WHITE)
                txt.paint.isFakeBoldText = position == 0
                txt.text = list[i]
                txt.isClickable = true
                txt.setOnClickListener {
                    if (position > 0) {
                        listener?.onWxGridClick(position, i, (list[i] == "[BLOB]"), list[i])
                    }
                }
                baseLayout.addView(txt)
            }
        }
    }

}