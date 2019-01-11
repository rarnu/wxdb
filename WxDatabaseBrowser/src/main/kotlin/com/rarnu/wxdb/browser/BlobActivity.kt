package com.rarnu.wxdb.browser

import android.app.Activity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_blob.*
import com.rarnu.kt.android.*

class BlobActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar.title = intent.getStringExtra("Title")
        setContentView(R.layout.activity_blob)
        tv_blobInfo.text = intent.getStringExtra("BlobParseInfo")
    }

}