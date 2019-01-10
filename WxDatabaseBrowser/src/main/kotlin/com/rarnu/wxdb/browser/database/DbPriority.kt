package com.rarnu.wxdb.browser.database

import com.rarnu.wxdb.browser.ref.WxClassLoader.clzCursorFactory
import com.rarnu.wxdb.browser.ref.WxClassLoader.clzDatabaseErrorHandler
import com.rarnu.wxdb.browser.ref.WxClassLoader.clzSQLiteDatabase
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class DbPriority(pwd: String?) : DbIntf(pwd) {

    override fun initDb(pwd: String?): Any? {
        val mLoad = clzSQLiteDatabase?.getDeclaredMethod("openOrCreateDatabase", String::class.java, ByteArray::class.java, clzCursorFactory, clzDatabaseErrorHandler)
        mLoad?.isAccessible = true
        return mLoad?.invoke(null, File(Config.basePath(), "priority.db").absolutePath, pwd!!.toByteArray(), null, null)
    }

}