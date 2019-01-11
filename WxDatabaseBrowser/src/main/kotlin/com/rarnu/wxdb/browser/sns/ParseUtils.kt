package com.rarnu.wxdb.browser.sns

import com.rarnu.wxdb.browser.ref.WxClassLoader

object ParseUtils {

    fun ParseFrom(dataBasePath: String, tableName: String, fieldName: String, content: ByteArray) =
            NewParser(content, WxClassLoader.parserMap["$dataBasePath.$tableName.$fieldName"]).parseFrom()
}

