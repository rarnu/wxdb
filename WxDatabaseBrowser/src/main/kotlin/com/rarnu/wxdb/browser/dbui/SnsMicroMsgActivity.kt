package com.rarnu.wxdb.browser.dbui

import android.content.Intent
import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.BlobActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbSnsMicroMsg
import com.rarnu.wxdb.browser.ref.WxClassLoader
import com.rarnu.wxdb.browser.sns.NewParser
import com.rarnu.wxdb.browser.sns.ParseInfo

class SnsMicroMsgActivity : BaseTableActivity() {

    override fun initDb() = DbSnsMicroMsg()

    override fun titleResId() = R.string.title_sns

    override fun showBlobData(row: Int, col: Int, blob: ByteArray) {
        val clz = WxClassLoader.parserMap["${db.dbName}.$currentTableName.${currentTableField[col].str}"]
        ParseInfo.count = 0
        val str = NewParser(blob, clz).parseFrom()
        val intent = Intent(this, BlobActivity::class.java)
        intent.putExtra("blobParseInfo", "$str")
        intent.putExtra("title", "${db.dbName}.$currentTableName.${currentTableField[col].str}")
        startActivity(intent)
    }
}