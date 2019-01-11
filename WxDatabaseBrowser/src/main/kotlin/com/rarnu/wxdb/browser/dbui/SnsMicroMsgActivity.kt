package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbSnsMicroMsg

class SnsMicroMsgActivity : BaseTableActivity() {

    override fun initDb() = DbSnsMicroMsg()

    override fun titleResId() = R.string.title_sns


    override fun showBlobData(row: Int, col: Int, blob: ByteArray) {
        // currentTableName
        // col


    }
}