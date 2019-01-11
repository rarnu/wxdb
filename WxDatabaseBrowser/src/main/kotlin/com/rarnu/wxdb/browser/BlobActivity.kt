package com.rarnu.wxdb.browser

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.rarnu.kt.android.showActionBack
import kotlinx.android.synthetic.main.activity_blob.*

class BlobActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_blob)
        actionBar.title = intent.getStringExtra("Title")
        showActionBack()
        tv_blobInfo.text = intent.getStringExtra("BlobParseInfo")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}