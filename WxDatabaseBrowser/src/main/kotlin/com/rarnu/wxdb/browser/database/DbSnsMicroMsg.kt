package com.rarnu.wxdb.browser.database

import android.database.sqlite.SQLiteDatabase
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class DbSnsMicroMsg(pwd: String? = null) : DbIntf(pwd) {
    override fun initDb(pwd: String?) = SQLiteDatabase.openDatabase(File(Config.basePath(), "sns.db").absolutePath, null, 0)
}