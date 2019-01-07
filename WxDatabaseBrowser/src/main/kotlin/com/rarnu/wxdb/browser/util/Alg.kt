package com.rarnu.wxdb.browser.util

import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

object Alg {

    fun loadDeviceId(): String {
        val fDev = File(Config.basePath(), "device.cfg")
        var str: String? = ""
        try {
            val ois = ObjectInputStream(FileInputStream(fDev))
            @Suppress("UNCHECKED_CAST")
            val map = ois.readObject() as Map<Int, String>
            str = map[258]
            ois.close()
        } catch (e: Throwable) {

        }
        if (str == null || str == "") {
            str = "1234567890ABCDEF"
        }
        return str
    }

    fun getUin(): String {
        var str = Utils.readFile(File(Config.basePath(), "uin.xml"))
        str = str.substring(str.indexOf("_auth_uin"))
        str = str.substring(0, str.indexOf("/>"))
        str = str.replace("_auth_uin", "").replace("value=", "")
        str = str.replace("\"", "").trim()
        return str
    }

    fun getLoginAccount(): String {
        var str = Utils.readFile(File(Config.basePath(), "account.xml"))
        str = str.substring(str.indexOf("login_weixin_username"))
        str = str.substring(0, str.indexOf("</"))
        str = str.replace("login_weixin_username", "")
        str = str.replace("\"", "").replace(">", "").trim()
        return str
    }

    fun getEnMicroMsgPassword(): String {
        val did = loadDeviceId()
        val uin = getUin()
        val md5 = Utils.md5Encode(did + uin)
        return md5.substring(0, 7).toLowerCase()
    }

    fun getIndexMicroMsgPassword(): String {
        val did = loadDeviceId()
        val uin = getUin()
        val acc = getLoginAccount()
        val md5 = Utils.md5Encode(uin + did + acc)
        return md5.substring(0, 7).toLowerCase()
    }

    fun getUserFolder(): String {
        val uin = getUin()
        val md5 = Utils.md5Encode("mm$uin")
        return md5
    }

}