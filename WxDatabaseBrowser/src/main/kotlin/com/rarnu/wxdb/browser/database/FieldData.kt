package com.rarnu.wxdb.browser.database

import java.io.Serializable

class FieldData: Serializable {
    var isBlob = false
    var str = ""
    var blob: ByteArray? = null

    constructor(s: String) {
        str = s
    }

    constructor(b: ByteArray) {
        isBlob = true
        blob = b
    }
}