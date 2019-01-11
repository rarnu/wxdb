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
        val sb = StringBuffer()
        val commonType = listOf("int", "boolean", "float", "long", "byte[]", "double", "java.lang.String")
        if (!(fieldName.equals("") && fieldType.equals(""))) {
            if (!commonType.contains(fieldType)) {
                sb.append(spaceStr(count, true)).append(fieldType).append(" ").append(fieldName).append(" => ")
                    .append(fieldValue).append("\n")
            } else {
                sb.append(spaceStr(count, false)).append(fieldType).append(" ").append(fieldName).append(" => ")
                    .append(fieldValue).append("\n")
            }

            if (childList.isNotEmpty()) {
                count++
                for (child in childList) {
                    sb.append(child.toString())
                }
            }
        }

        return sb.toString()
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