package com.rarnu.wxdb.browser.sns

class ParseInfo {

    companion object {
        var count = 0
    }

    var fieldValue: String? = ""
    var fieldType: String? = ""
    var fieldName: String? = ""
    var childList = mutableListOf<ParseInfo>()

    override fun toString(): String {
        var sb = ""
        val commonType = listOf("int", "boolean", "float", "long", "byte[]", "double", "java.lang.String")
        if (fieldName != "" || fieldType != "") {
            sb += if (!commonType.contains(fieldType)) {
                "${spaceStr(count, true)}$fieldType $fieldName => $fieldValue\n"
            } else {
                "${spaceStr(count, false)}$fieldType $fieldName => $fieldValue\n"
            }
            if (childList.isNotEmpty()) {
                count++
                childList.forEach { sb += "$it" }
            }
        }
        return sb
    }

    private fun spaceStr(index: Int, isAdd: Boolean): String {
        var ret = ""
        var count = index
        while (count > 0) {
            ret += "|    "
            count--
        }
        return if (isAdd) "$ret+--" else "$ret--"
    }
}