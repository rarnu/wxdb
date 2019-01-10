package com.rarnu.wxdb.browser.dbui

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import com.rarnu.kt.android.*
import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.database.DbEnMicroMsg
import com.rarnu.wxdb.browser.database.FieldData
import com.rarnu.wxdb.browser.grid.WxGridAdapter
import com.rarnu.wxdb.browser.util.Alg
import java.io.File
import kotlin.concurrent.thread
import com.rarnu.wxdb.browser.R

class EnMicroMsgActivity : BaseTableActivity(), AdapterView.OnItemSelectedListener, WxGridAdapter.WxGridListener {

    override fun initDb() = DbEnMicroMsg(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_micro_msg

    override fun onWxGridClick(row: Int, col: Int, data: FieldData) {

        if (!data.isBlob && data.str.trim() != "") {
            findFile(data.str.trim()) {
                if (it == null) {
                    alert("$currentTableName [$row, $col]", data.str, resStr(R.string.btn_ok)) { }
                } else {
                    val bmp = try {
                        BitmapFactory.decodeFile(it.absolutePath)
                    } catch (e: Throwable) {
                        null
                    }
                    if (bmp != null) {
                        val iv = ImageView(this)
                        iv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        iv.setImageBitmap(bmp)
                        AlertDialog.Builder(this).setTitle("$currentTableName [$row, $col]").setMessage(data.str)
                                .setView(iv).setPositiveButton(R.string.btn_ok, null).show()
                    } else {
                        alert("$currentTableName [$row, $col]", data.str, resStr(R.string.btn_ok)) { }
                    }
                }
            }
        }
    }

    private fun findFile(fn: String, callback: (f: File?) -> Unit) = thread {
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
}