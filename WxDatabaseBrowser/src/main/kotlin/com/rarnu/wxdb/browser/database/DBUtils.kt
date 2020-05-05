package com.rarnu.wxdb.browser.database

import android.graphics.Bitmap
import com.rarnu.android.UI
import com.rarnu.android.dip2px
import com.rarnu.android.runOnMainThread
import com.rarnu.wxdb.browser.util.runCommand
import java.io.File
import kotlin.concurrent.thread

object DBUtils {

    fun findChatImageFile(fn: String, callback: (f: File?) -> Unit) = thread {
        var img: File? = null
        var imgPath = fn
        imgPath = imgPath.replace("THUMBNAIL_DIRPATH://", "")
        imgPath = imgPath.replace("\n", "").replace("\r", "").replace("\"", "").trim()

        runCommand {
            runAsRoot = true
            commands.add("find /sdcard/tencent/MicroMsg -name \"$imgPath\"")
            result { output, _ ->
                val paths = output.trim().split("\n")
                for (s in paths) {
                    if (s.contains("temp")) continue
                    var path = s
                    if (path.contains("emoji")) {
                        path += "_cover"
                    }
                    if (File(path + "hd").exists()) {
                        path += "hd"
                    }
                    val tmpImg = File(path)
                    if (tmpImg.exists()) {
                        img = tmpImg
                        break
                    }
                }
                runOnMainThread { callback(img) }
            }
        }
    }

    fun findSnsImageFile(mediaId: String?, callback: (f: File?) -> Unit) = thread {
        runCommand {
            runAsRoot = true
            commands.add("find /sdcard/tencent/MicroMsg -name \"*$mediaId\"")
            result { output, _ ->
                val paths = output.trim().split("\n")
                var p = ""
                for (s in paths) {
                    if (s.contains("/snsb_")) {
                        p = s
                    }
                    if (s.contains("/snsu_") && (p == "" || p.contains("/snst_"))) {
                        p = s
                    }
                    if (s.contains("/snst_") && p == "") {
                        p = s
                    }
                }
                val f = File(p)
                val img = if (f.exists()) f else null
                runOnMainThread { callback(img) }
            }
        }
    }

    fun getSnsImageHeight(bmp: Bitmap): Int {
        val wfix = UI.width - 8.dip2px()
        return if (bmp.width <= wfix) {
            bmp.height
        } else {
            ((wfix * bmp.height) / bmp.width)
        }
    }

}