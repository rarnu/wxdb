package com.rarnu.wxdb.browser

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Gravity
import android.view.MenuItem
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.rarnu.kt.android.*
import com.rarnu.wxdb.browser.sns.SnsInfo
import kotlinx.android.synthetic.main.actiivty_sns_detail.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class SnsDetailActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actiivty_sns_detail)

        actionBar.title = resStr(R.string.title_sns_detail)
        showActionBack()

        val info = intent.getSerializableExtra("info") as SnsInfo

        tvAuthor.text = "${info.authorName} [${info.authorId}]"
        tvTimestamp.text = SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(Date(info.timestamp * 1000))
        tvContent.text = info.content

        info.mediaList.forEach { m ->
            findFile(m) { f ->
                if (f != null) {
                    val bmp = try {
                        BitmapFactory.decodeFile(f.absolutePath)
                    } catch (e: Throwable) {
                        null
                    }
                    if (bmp != null) {
                        val iv = ImageView(this)
                        val lp = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, getImageHeight(bmp))
                        lp.topMargin = 4.dip2px()
                        iv.layoutParams = lp
                        iv.setImageBitmap(bmp)
                        iv.scaleType = ImageView.ScaleType.CENTER_INSIDE
                        layImages.addView(iv)
                    }
                }
            }
        }

        info.likes.forEach {
            val like = TextView(this)
            like.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 20.dip2px())
            like.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            like.text = "${it.userName} [${it.userId}]"
            layLike.addView(like)
        }

        info.comments.forEach {
            val comment = TextView(this)
            comment.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 24.dip2px())
            comment.gravity = Gravity.LEFT or Gravity.CENTER_VERTICAL
            var cstr = "${it.authorName}: "
            var cstrfull = "${it.authorName}[${it.authorId}]: "
            if (it.toUser != null && it.toUser != "") {
                cstr += "to ${it.toUser}: "
                cstrfull += "to ${it.toUser}[${it.toUserId}]: "
            }
            cstr += it.content
            cstrfull += it.content
            comment.text = cstr
            comment.setSingleLine(true)
            comment.maxLines = 1
            comment.ellipsize = TextUtils.TruncateAt.END
            comment.isClickable = true
            comment.setOnClickListener {
                alert(resStr(R.string.text_comment), cstrfull, resStr(R.string.btn_ok)) { }
            }
            layComment.addView(comment)
        }

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun getImageHeight(bmp: Bitmap): Int {
        val wfix = UI.width - 8.dip2px()
        return if (bmp.width <= wfix) {
            bmp.height
        } else {
            ((wfix * bmp.height) / bmp.width)
        }
    }


    private fun findFile(fn: String, callback: (f: File?) -> Unit) = thread {
        var img: File?
        runCommand {
            runAsRoot = true
            commands.add("find /sdcard/tencent/MicroMsg -name \"*$fn\"")
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
                img = if (f.exists()) f else null
                runOnMainThread { callback(img) }
            }
        }
    }

}