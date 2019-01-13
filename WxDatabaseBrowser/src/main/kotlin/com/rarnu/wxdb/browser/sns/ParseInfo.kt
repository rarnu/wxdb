package com.rarnu.wxdb.browser.sns

import com.unnamed.b.atv.model.TreeNode
import java.io.Serializable

class ParseInfo: Serializable {

    var fieldValue: String? = ""
    var fieldType: String? = ""
    var fieldName: String? = ""
    var childList = mutableListOf<ParseInfo>()

    fun toTree(): TreeNode? {
        if (fieldType == null || fieldType == "") {
            return null
        }
        val thisObj = TreeNode(this)
        if (childList.isNotEmpty()) {
            childList.forEach {
                val t = it.toTree()
                if (t != null) {
                    thisObj.addChild(t)
                }
            }
        }
        return thisObj
    }
}