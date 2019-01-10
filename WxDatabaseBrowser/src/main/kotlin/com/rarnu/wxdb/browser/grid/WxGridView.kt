package com.rarnu.wxdb.browser.grid

import android.content.Context
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.ListView
import com.rarnu.wxdb.browser.database.FieldData

class WxGridView(context: Context) : HorizontalScrollView(context) {

    private val list = mutableListOf<List<FieldData>>()
    private val adapter: WxGridAdapter
    var listener: WxGridAdapter.WxGridListener? = null
        set(value) {
            adapter.listener = value
        }

    init {
        isHorizontalScrollBarEnabled = false
        isVerticalScrollBarEnabled = false
        val lay = LinearLayout(context)
        lay.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT)
        lay.orientation = LinearLayout.HORIZONTAL
        addView(lay)

        val lv = ListView(context)
        lv.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT)
        lv.isHorizontalScrollBarEnabled = false
        lv.isVerticalScrollBarEnabled = false
        lay.addView(lv)

        adapter = WxGridAdapter(context, list)
        lv.adapter = adapter
    }

    fun setItems(l: MutableList<List<FieldData>>) {
        list.clear()
        list.addAll(l)
        adapter.notifyDataSetChanged()
    }

}