package com.rarnu.wxdb.browser.database

import android.database.Cursor

interface DbIntf {

    fun getTableList(): MutableList<String>

    fun getTableCount(tableName: String, callback: (rowCount: Int, pageCount: Int) -> Unit)

    fun queryTable(tableName: String): Cursor?

    fun executeSQL(sql: String): Cursor?

    fun close()

}