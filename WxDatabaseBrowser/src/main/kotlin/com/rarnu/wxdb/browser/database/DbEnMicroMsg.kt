package com.rarnu.wxdb.browser.database

import android.database.Cursor
import android.util.Log
import com.rarnu.wxdb.browser.util.Config
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SQLiteDatabaseHook
import java.io.File

class DbEnMicroMsg(pwd: String) : DbIntf {


    private val size = 50
    private var password = ""
    private val db: SQLiteDatabase

    init {
        val hook = object : SQLiteDatabaseHook {
            override fun preKey(database: SQLiteDatabase) {}
            override fun postKey(database: SQLiteDatabase) {
                try {
                    database.rawExecSQL("PRAGMA cipher_migrate;")
                } catch (e: Throwable) {
                }
            }
        }
        password = pwd
        db = SQLiteDatabase.openOrCreateDatabase(File(Config.basePath(), "msg.db"), password, null, hook)
        Log.e("DB", "init EnMicroMsg => $password, $db")
    }

    override fun getTableList(): MutableList<String> {
        val list = mutableListOf<String>()
        val c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null)
        if (c != null) {
            c.moveToFirst()
            while (c.moveToNext()) {
                list.add(c.getString(0))
            }
            c.close()
        }
        return list
    }

    override fun getTableCount(tableName: String, callback: (rowCount: Int, pageCount: Int) -> Unit) {
        var rowCount = 0
        var pageCount: Int

        val c = try {
            db.rawQuery("SELECT count(*) FROM $tableName", null)
        } catch (e: Throwable) {
            Log.e("DB", "getTableCount.error => $e")
            null
        }
        if (c != null) {
            c.moveToFirst()
            rowCount = c.getInt(0)
            c.close()
        }
        pageCount = rowCount / size
        if (rowCount % size != 0) {
            pageCount++
        }
        callback(rowCount, pageCount)
    }

    override fun queryTable(tableName: String): Cursor? {
        val c = try {
            db.rawQuery("SELECT * FROM $tableName", null)
        } catch (e: Throwable) {
            Log.e("DB", "queryTable.error => $e")
            null
        }
        return c
    }

    override fun executeSQL(sql: String): Cursor? {
        val c = try {
            db.rawQuery(sql, null)
        } catch (e: Throwable) {
            null
        }
        return c
    }

    override fun close() {
        db.close()
    }

}