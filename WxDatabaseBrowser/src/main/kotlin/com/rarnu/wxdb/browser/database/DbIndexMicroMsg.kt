package com.rarnu.wxdb.browser.database

import android.database.Cursor
import android.util.Log
import com.rarnu.wxdb.browser.util.Config
import com.rarnu.wxdb.browser.ref.WxClassLoader.clzCursorFactory
import com.rarnu.wxdb.browser.ref.WxClassLoader.clzDatabaseErrorHandler
import com.rarnu.wxdb.browser.ref.WxClassLoader.clzSQLiteDatabase
import java.io.File
import java.lang.reflect.Method

class DbIndexMicroMsg(pwd: String): DbIntf {

    private val size = 50
    private var db: Any? = null
    private var mQuery: Method? = null
    private var mClose: Method? = null
    private var password = ""

    init {
        password = pwd

        val mLoad = clzSQLiteDatabase?.getDeclaredMethod("openOrCreateDatabase", String::class.java, ByteArray::class.java, clzCursorFactory, clzDatabaseErrorHandler)
        mLoad?.isAccessible = true
        db = mLoad?.invoke(null, File(Config.basePath(), "index.db").absolutePath, pwd.toByteArray(), null, null)
        mQuery = clzSQLiteDatabase?.getDeclaredMethod("rawQuery", String::class.java, Array<Any>::class.java)
        mQuery?.isAccessible = true
        mClose = clzSQLiteDatabase?.superclass?.getDeclaredMethod("close")
        mClose?.isAccessible = true
    }

    override fun getTableList(): MutableList<String> {
        val list = mutableListOf<String>()
        val c = try {
            mQuery?.invoke(db, "SELECT name FROM sqlite_master WHERE type='table' ORDER BY name", null) as? Cursor
        } catch (e: Throwable) {
            Log.e("DB", "getTableList.error => $e")
            null
        }
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

    override fun queryTable(tableName: String): Cursor? {
        val c = try {
            mQuery?.invoke(db, "SELECT * FROM $tableName", null) as? Cursor
        } catch (e: Throwable) {
            Log.e("DB", "queryTable.error => $e")
            null
        }
        return c
    }

    override fun executeSQL(sql: String): Cursor? {
        val c = try {
            mQuery?.invoke(db, sql, null) as? Cursor
        } catch (e: Throwable) {
            null
        }
        return c
    }

    override fun close() {
        mClose?.invoke(db)
    }
}