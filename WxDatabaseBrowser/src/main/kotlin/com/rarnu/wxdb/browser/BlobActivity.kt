package com.rarnu.wxdb.browser

import android.app.Activity
import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.MenuItem
import android.view.ViewGroup
import android.widget.ImageView
import com.rarnu.kt.android.BackActivity
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.showActionBack
import com.rarnu.wxdb.browser.database.DBUtils
import com.rarnu.wxdb.browser.sns.ParseInfo
import com.rarnu.wxdb.browser.sns.ui.SnsNodeHolder
import com.unnamed.b.atv.model.TreeNode
import com.unnamed.b.atv.view.AndroidTreeView
import kotlinx.android.synthetic.main.activity_blob.*

class BlobActivity : BackActivity(), TreeNode.TreeNodeClickListener {

    private val root = TreeNode.root()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blob)
        actionBar.title = intent.getStringExtra("title")
        val info = intent.getSerializableExtra("blobParseInfo") as ParseInfo
        root.addChild(info.toTree())
        val treeView = AndroidTreeView(this, root)
        treeView.setDefaultAnimation(true)
        treeView.setUse2dScroll(true)
        treeView.setDefaultViewHolder(SnsNodeHolder::class.java)
        treeView.setDefaultContainerStyle(R.style.TreeNodeStyleCustom)
        layBlob.addView(treeView.view)
        treeView.expandLevel(1)
        treeView.setDefaultNodeClickListener(this)
    }

    override fun onClick(node: TreeNode?, value: Any?) {
        if (value != null && value is ParseInfo) {
            if (value.fieldName == "Id" && value.fieldType == "java.lang.String" && value.fieldValue != null && value.fieldValue != "") {
                DBUtils.findSnsImageFile(value.fieldValue) {
                    if (it != null) {
                        val bmp = try {
                            BitmapFactory.decodeFile(it.absolutePath)
                        } catch (e: Throwable) {
                            null
                        }
                        if (bmp != null) {
                            val iv = ImageView(this)
                            iv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                            iv.setImageBitmap(bmp)
                            AlertDialog.Builder(this)
                                    .setTitle(R.string.title_sns_image).setMessage(resStr(R.string.title_image_id, value.fieldValue))
                                    .setView(iv).setPositiveButton(R.string.btn_ok, null).show()
                        }
                    }
                }
            }
        }
    }


}