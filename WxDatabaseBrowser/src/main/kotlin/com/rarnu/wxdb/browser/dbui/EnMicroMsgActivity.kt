package com.rarnu.wxdb.browser.dbui

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.view.ViewGroup
import android.widget.ImageView
import com.rarnu.kt.android.alert
import com.rarnu.kt.android.resStr
import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DBUtils
import com.rarnu.wxdb.browser.database.DbEnMicroMsg
import com.rarnu.wxdb.browser.util.Alg

class EnMicroMsgActivity : BaseTableActivity() {

    override fun initDb() = DbEnMicroMsg(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_micro_msg

    override fun showStringData(row: Int, col: Int, str: String) {
        DBUtils.findChatImageFile(str.trim()) {
            if (it == null) {
                alert("$currentTableName [$row, $col]", str, resStr(R.string.btn_ok)) { }
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
                    AlertDialog.Builder(this).setTitle("$currentTableName [$row, $col]").setMessage(str)
                            .setView(iv).setPositiveButton(R.string.btn_ok, null).show()
                } else {
                    alert("$currentTableName [$row, $col]", str, resStr(R.string.btn_ok)) { }
                }
            }
        }
    }


}