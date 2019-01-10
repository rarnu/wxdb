package com.rarnu.wxdb.browser.database

import com.rarnu.wxdb.browser.util.Config
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import java.io.File

class DbFavorite(pwd: String?) : DbIntf(pwd) {
    override fun initDb(pwd: String?) = try {
        SQLiteDatabase.openOrCreateDatabase(File(Config.basePath(), "favorite.db"), pwd, null, object : SQLiteDatabaseHook {
            override fun preKey(database: SQLiteDatabase) {}
            override fun postKey(database: SQLiteDatabase) {
                try {
                    database.rawExecSQL("PRAGMA cipher_migrate;")
                } catch (e: Throwable) {
                }
            }
        })
    } catch (e: Throwable) {
        null
    }

}