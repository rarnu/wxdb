package com.rarnu.wxdb.browser

import android.os.Bundle
import com.rarnu.android.BackActivity
import com.rarnu.wxdb.browser.ui.HexAdapter
import kotlinx.android.synthetic.main.activity_hexview.*
import java.nio.charset.Charset

class BlobHexActivity: BackActivity() {

    private lateinit var adapter: HexAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hexview)
        actionBar?.title = intent.getStringExtra("title")
        val data = intent.getByteArrayExtra("data")
        tvBlobString.text = data.toString(Charset.defaultCharset())
        adapter = HexAdapter(this, data.toMutableList())
        gvBlobHex.adapter = adapter
    }
}