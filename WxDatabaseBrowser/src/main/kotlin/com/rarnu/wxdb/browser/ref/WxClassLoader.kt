package com.rarnu.wxdb.browser.ref

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import com.rarnu.kt.android.assetsIO
import com.rarnu.kt.android.runOnMainThread
import com.rarnu.wxdb.browser.util.Config
import dalvik.system.DexClassLoader
import java.io.File
import kotlin.concurrent.thread

object WxClassLoader {

    // index
    var clzSQLiteDatabase: Class<*>? = null
    var clzCursorFactory: Class<*>? = null
    var clzDatabaseErrorHandler: Class<*>? = null

    val parserMap = mutableMapOf<String, Class<*>?>()

    fun initClasses(ctx: Context, complete: () -> Unit) = thread {

        val wxPath7 = File(Config.basePath(), "wechat7.apk")
        if (!wxPath7.exists()) {
            ctx.assetsIO {
                src = "wechat7.apk"
                dest = wxPath7
            }
        }

        val oat7 = ctx.getDir("oat7", 0)
        if (!oat7.exists()) {
            oat7.mkdirs()
        }

        try {
            val cl7 = DexClassLoader(wxPath7.absolutePath, oat7.absolutePath, findSoPath(), ClassLoader.getSystemClassLoader())
            clzSQLiteDatabase = cl7.loadClass("com.tencent.wcdb.database.SQLiteDatabase")
            clzCursorFactory = cl7.loadClass("com.tencent.wcdb.database.SQLiteDatabase\$CursorFactory")
            clzDatabaseErrorHandler = cl7.loadClass("com.tencent.wcdb.DatabaseErrorHandler")

            parserMap["sns.SnsInfo.content"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.TimeLineObject")
            parserMap["sns.SnsInfo.attrBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.byd")
            parserMap["sns.SnsComment.curActionBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.bxl")
            parserMap["sns.SnsComment.refActionBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.bxl")
            parserMap["sns.SnsMedia.upload_buf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.ayy")
            parserMap["sns.snsExtInfo3.snsuser"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.bzj")
            parserMap["sns.adsnsinfo.content"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.TimeLineObject")
            parserMap["sns.adsnsinfo.attrBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.byd")

            parserMap["story.MMStoryInfo.content"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.cbx")
            parserMap["story.MMStoryInfo.attrBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.cbn")
            parserMap["story.MMStoryInfo.postBuf"] = cl7.loadClass("com.tencent.mm.protocal.protobuf.cbj")
            parserMap["story.StoryEditorInfo.baseItemData"] = cl7.loadClass("com.tencent.mm.bv.a")

            // parserMap["story.StoryRoomInfo.extbuf"] = cl7.loadClass("")

            // parserMap[""] = cl7.loadClass("")

        } catch (e: Throwable) {
            Log.e("DB", "initClasses.error = $e")
        }
        runOnMainThread { complete() }
    }

    @SuppressLint("SdCardPath")
    private fun findSoPath(): String? {
        val path1 = "/data/data/com.rarnu.wxdb.browser/lib"
        val f1 = File(path1, "libwcdb.so")
        if (f1.exists()) {
            return path1
        }
        return null
    }

}