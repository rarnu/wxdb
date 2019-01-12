package com.rarnu.wxdb.browser.sns.ui

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.sns.ParseInfo
import com.unnamed.b.atv.model.TreeNode
import kotlinx.android.synthetic.main.item_node.view.*

class SnsNodeHolder(context: Context?) : TreeNode.BaseNodeViewHolder<ParseInfo>(context) {

    override fun createNodeView(node: TreeNode, value: ParseInfo): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_node, null, false)
        view.tvText.text = "${value.fieldName}: ${value.fieldType} = ${value.fieldValue}"
        return view
    }

}