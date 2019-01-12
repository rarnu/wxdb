package com.rarnu.wxdb.browser.database

import android.database.sqlite.SQLiteDatabase
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class DbStory(pwd: String? = null) : DbIntf(pwd) {

    @Suppress("HasPlatformType")
    override fun initDb(pwd: String?) = try {
        SQLiteDatabase.openDatabase(File(Config.basePath(), "story.db").absolutePath, null, 0)
    } catch (e: Throwable) {
        null
    }

}