package com.rarnu.wxdb.browser.sns

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log

object ParseUtils {

    fun ParseFromTable(dataBasePath: String, tableName: String, fieldName: String, clz: Class<*>?): List<ParseInfo>? {
        // Parse some field from table
        if (tableName == "" || fieldName == "") return null
        val list = mutableListOf<ParseInfo>()
        val database = SQLiteDatabase.openDatabase(dataBasePath, null, 0)
        val cursor = queryTable(database, tableName)
        cursor!!.moveToFirst()
        while (cursor.moveToNext()) {
            val valueBytes = getValueBytes(cursor, fieldName)
            val parseNode = NewParser(valueBytes, clz).parseFrom()
            list.add(parseNode)
        }
        return list
    }

    private fun getValueBytes(cursor: Cursor, fieldName: String): ByteArray {
        val contentInx = cursor.getColumnIndex(fieldName)
        return cursor.getBlob(contentInx)
    }

    private fun queryTable(db: SQLiteDatabase?, tableName: String): Cursor? {
        val c = try {
            db?.rawQuery("SELECT * FROM $tableName", null)
        } catch (e: Throwable) {
            Log.e("DB", "queryTable.error => $e")
            null
        }
        return c
    }
}