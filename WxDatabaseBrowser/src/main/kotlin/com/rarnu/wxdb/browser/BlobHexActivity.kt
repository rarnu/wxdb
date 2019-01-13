package com.rarnu.wxdb.browser

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import com.rarnu.kt.android.showActionBack
import com.rarnu.wxdb.browser.ui.HexAdapter
import kotlinx.android.synthetic.main.activity_hexview.*
import java.nio.charset.Charset

class BlobHexActivity: Activity() {

    private lateinit var adapter: HexAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hexview)
        actionBar.title = intent.getStringExtra("title")
        showActionBack()
        val data = intent.getByteArrayExtra("data")
        tvBlobString.text = data.toString(Charset.defaultCharset())
        adapter = HexAdapter(this, data.toMutableList())
        gvBlobHex.adapter = adapter
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}