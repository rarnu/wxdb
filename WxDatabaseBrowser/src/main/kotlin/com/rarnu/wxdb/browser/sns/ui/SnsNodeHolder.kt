package com.rarnu.wxdb.browser.sns.ui

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.sns.ParseInfo
import com.unnamed.b.atv.model.TreeNode
import kotlinx.android.synthetic.main.item_node.view.*

class SnsNodeHolder(context: Context?) : TreeNode.BaseNodeViewHolder<ParseInfo>(context) {

    @SuppressLint("SetTextI18n", "InflateParams")
    override fun createNodeView(node: TreeNode, value: ParseInfo): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_node, null, false)
        val typ = value.fieldType?.replace("java.lang.", "")?.replace("java.util.", "")
        var obj = value.fieldValue?.replace(value.fieldType!!, "")
        if (obj != null && obj.contains("@")) {
            obj = "object"
        }
        if (typ == "String") {
            obj = "\"$obj\""
        }
        view.tvText.text = "${value.fieldName}: $typ = $obj"
        return view
    }

}