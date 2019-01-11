package com.rarnu.wxdb.browser.database

import android.database.sqlite.SQLiteDatabase
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class DbAuxData(pwd: String? = null) : DbIntf(pwd) {

    @Suppress("HasPlatformType")
    override fun initDb(pwd: String?) = SQLiteDatabase.openDatabase(File(Config.basePath(), "aux.db").absolutePath, null, 0)

}