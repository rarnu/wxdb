package com.rarnu.wxdb.browser.database

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class DbSnsMicroMsg: DbIntf {

    private val size = 50
    private var db: SQLiteDatabase? = null

    init {
        db = SQLiteDatabase.openDatabase(File(Config.basePath(), "sns.db").absolutePath, null, 0)
    }

    override fun getTableList(): MutableList<String> {
        val list = mutableListOf<String>()
        val c = db?.rawQuery("SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null)
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
            db?.rawQuery("SELECT count(*) FROM $tableName", null)
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
            db?.rawQuery("SELECT * FROM $tableName", null)
        } catch (e: Throwable) {
            Log.e("DB", "queryTable.error => $e")
            null
        }
        return c
    }

    override fun executeSQL(sql: String): Cursor? {
        val c = try {
            db?.rawQuery(sql, null)
        } catch (e: Throwable) {
            null
        }
        return c
    }

    override fun close() {
        db?.close()
    }
}