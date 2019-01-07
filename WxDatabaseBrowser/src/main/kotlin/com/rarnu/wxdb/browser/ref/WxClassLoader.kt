package com.rarnu.wxdb.browser.ref

import android.content.Context
import android.util.Log
import com.rarnu.kt.android.assetsIO
import com.rarnu.kt.android.runOnMainThread
import com.rarnu.wxdb.browser.util.Config
import dalvik.system.DexClassLoader
import java.io.File
import kotlin.concurrent.thread

object WxClassLoader {

    // sns
    var snsDetailParser: Class<*>? = null
    var snsDetail: Class<*>? = null
    var snsObject: Class<*>? = null

    // index
    var clzSQLiteDatabase: Class<*>? = null
    var clzCursorFactory: Class<*>? = null
    var clzDatabaseErrorHandler: Class<*>? = null

    fun initClasses(ctx: Context, complete: () -> Unit) = thread {
        val wxPath = File(Config.basePath(), "wechat.apk")
        if (!wxPath.exists()) {
            ctx.assetsIO {
                src = "wechat.apk"
                dest = wxPath
            }
        }
        val wxPath7 = File(Config.basePath(), "wechat7.apk")
        if (!wxPath7.exists()) {
            ctx.assetsIO {
                src = "wechat7.apk"
                dest = wxPath7
            }
        }

        val oat = ctx.getDir("oat", 0)
        if (!oat.exists()) {
            oat.mkdirs()
        }

        val oat7 = ctx.getDir("oat7", 0)
        if (!oat7.exists()) {
            oat7.mkdirs()
        }

        try {
            val cl = DexClassLoader(wxPath.absolutePath, oat.absolutePath, null, ClassLoader.getSystemClassLoader())
            snsDetailParser = cl.loadClass("com.tencent.mm.plugin.sns.f.i")
            snsDetail = cl.loadClass("com.tencent.mm.protocal.b.atp")
            snsObject = cl.loadClass("com.tencent.mm.protocal.b.aqi")
        } catch (e: Throwable) {

        }

        try {
            val cl7 = DexClassLoader(wxPath7.absolutePath, oat7.absolutePath, findSoPath(), ClassLoader.getSystemClassLoader())
            clzSQLiteDatabase = cl7.loadClass("com.tencent.wcdb.database.SQLiteDatabase")
            clzCursorFactory = cl7.loadClass("com.tencent.wcdb.database.SQLiteDatabase\$CursorFactory")
            clzDatabaseErrorHandler = cl7.loadClass("com.tencent.wcdb.DatabaseErrorHandler")
        } catch (e: Throwable) {
            Log.e("DB", "initClasses.error = $e")
        }
        runOnMainThread { complete() }
    }

    fun findSoPath(): String? {
        val path1 = "/data/data/com.rarnu.wxdb.browser/lib"
        val f1 = File(path1, "libwcdb.so")
        if (f1.exists()) {
            return path1
        }
        return null
    }

}