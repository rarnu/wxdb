package com.rarnu.wxdb.browser

import android.app.AlertDialog
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.ViewGroup
import android.widget.ImageView
import com.rarnu.android.BackActivity
import com.rarnu.android.resStr
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
        actionBar?.title = intent.getStringExtra("title")
        val info = intent.getSerializableExtra("blobParseInfo") as ParseInfo
        root.addChild(info.toTree())
        val treeView = AndroidTreeView(this, root).apply {
            setDefaultAnimation(true)
            setUse2dScroll(true)
            setDefaultViewHolder(SnsNodeHolder::class.java)
            setDefaultContainerStyle(R.style.TreeNodeStyleCustom)

        }
        layBlob.addView(treeView.view)
        with(treeView) {
            expandLevel(1)
            setDefaultNodeClickListener(this@BlobActivity)
        }
    }

    override fun onClick(node: TreeNode?, value: Any?) {
        if (value != null && value is ParseInfo) {
            if (value.fieldName == "Id" && value.fieldType == "java.lang.String" && value.fieldValue != null && value.fieldValue != "") {
                DBUtils.findSnsImageFile(value.fieldValue) {
                    if (it != null) {
                        val bmp = try { BitmapFactory.decodeFile(it.absolutePath) } catch (e: Throwable) { null }
                        if (bmp != null) {
                            AlertDialog.Builder(this)
                                    .setTitle(R.string.title_sns_image).setMessage(resStr(R.string.title_image_id, value.fieldValue))
                                    .setView(ImageView(this).apply {
                                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                                        setImageBitmap(bmp)
                                    }).setPositiveButton(R.string.btn_ok, null).show()
                        }
                    }
                }
            }
        }
    }


}