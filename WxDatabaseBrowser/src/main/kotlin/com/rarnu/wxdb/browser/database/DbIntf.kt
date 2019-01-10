package com.rarnu.wxdb.browser.database

import android.database.Cursor
import android.util.Log
import java.lang.reflect.Method

abstract class DbIntf(pwd: String? = null) {

    private val size = 50
    private var db: Any? = null
    private var mQuery: Method? = null
    private var mClose: Method? = null

    abstract fun initDb(pwd: String?): Any?

    init {
        db = initDb(pwd)
        mQuery = try {
            db?.javaClass?.getDeclaredMethod("rawQuery", String::class.java, Array<Any>::class.java)
        } catch (e: Throwable) {
            db?.javaClass?.getDeclaredMethod("rawQuery", String::class.java, Array<String>::class.java)
        }
        mQuery?.isAccessible = true
        mClose = try {
            db?.javaClass?.getDeclaredMethod("close")
        } catch (e: Throwable) {
            db?.javaClass?.superclass?.getDeclaredMethod("close")
        }
        mClose?.isAccessible = true
    }

    fun getTableList(): MutableList<String> {
        val list = mutableListOf<String>()
        val c = try {
            mQuery?.invoke(db, "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null) as? Cursor
        } catch (e: Throwable) {
            null
        }
        if (c != null) {
            c.moveToFirst()
            if (c.count > 0) {
                do {
                    list.add(c.getString(0))
                } while (c.moveToNext())
            }
            c.close()
        }
        return list
    }

    fun getTableCount(tableName: String, callback: (rowCount: Int, pageCount: Int) -> Unit) {
        var rowCount = 0
        var pageCount: Int

        val c = try {
            mQuery?.invoke(db, "SELECT count(*) FROM $tableName", null) as? Cursor
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

    fun queryTable(tableName: String): Cursor? {
        val c = try {
            mQuery?.invoke(db, "SELECT * FROM $tableName", null) as? Cursor
        } catch (e: Throwable) {
            Log.e("DB", "queryTable.error => $e")
            null
        }
        return c
    }

    fun executeSQL(sql: String): Cursor? {
        val c = try {
            mQuery?.invoke(db, sql, null) as? Cursor
        } catch (e: Throwable) {
            null
        }
        return c
    }

    fun close() {
        mClose?.invoke(db)
    }

}