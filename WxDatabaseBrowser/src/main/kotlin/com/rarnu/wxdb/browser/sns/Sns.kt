package com.rarnu.wxdb.browser.sns

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import com.rarnu.wxdb.browser.util.Config
import java.io.File

class Sns {

    val parser = Parser()
    val snsList = mutableListOf<SnsInfo>()
    var currentUserId = ""

    @Throws(Throwable::class)
    fun run() {
        queryDatabase()
    }

    @Throws(Throwable::class)
    fun queryDatabase() {
        val dbPath = File(Config.basePath(), "sns.db")
        if (!dbPath.exists()) {
            throw Exception("DB file not found")
        }
        snsList.clear()
        val database = SQLiteDatabase.openDatabase(dbPath.absolutePath, null, 0)
        getCurrentUserIdFromDatabase(database)
        val cursor = database.query("SnsInfo", arrayOf("SnsId", "userName", "createTime", "content", "attrBuf"), "", null, "", "", "createTime DESC", "")
        while (cursor.moveToNext()) {
            addSnsInfoFromCursor(cursor)
        }
        cursor.close()
        database.close()
    }

    @Throws(Throwable::class)
    fun getCurrentUserIdFromDatabase(database: SQLiteDatabase) {
        val cursor = database.query("snsExtInfo3", arrayOf("userName"), "ROWID=?", arrayOf("1"), "", "", "", "1")
        if (cursor.moveToNext()) {
            currentUserId = cursor.getString(cursor.getColumnIndex("userName"))
        }
        cursor.close()
    }

    @Throws(Throwable::class)
    fun addSnsInfoFromCursor(cursor: Cursor) {
        val snsDetailBin = cursor.getBlob(cursor.getColumnIndex("content"))
        val snsObjectBin = cursor.getBlob(cursor.getColumnIndex("attrBuf"))
        val newSns = parser.parseSnsAllFromBin(snsDetailBin, snsObjectBin) ?: return

        if ((newSns.authorName == null || newSns.authorName == "") && (newSns.content == null || newSns.content == "")) {
            return
        }

        for (i in 0 until snsList.size) {
            if (snsList[i].id == newSns.id) {
                return
            }
        }

        if (newSns.authorId == currentUserId) {
            newSns.isCurrentUser = true
        }

        for (i in 0 until newSns.comments.size) {
            if (newSns.comments[i].authorId == currentUserId) {
                newSns.comments[i].isCurrentUser = true
            }
        }
        for (i in 0 until newSns.likes.size) {
            if (newSns.likes[i].userId == currentUserId) {
                newSns.likes[i].isCurrentUser = true
            }
        }

        snsList.add(newSns)
    }
}