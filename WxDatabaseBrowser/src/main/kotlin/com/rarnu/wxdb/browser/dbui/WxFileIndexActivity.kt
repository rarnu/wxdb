package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbWxFileIndex
import com.rarnu.wxdb.browser.util.Alg

class WxFileIndexActivity : BaseTableActivity() {

    override fun initDb() = DbWxFileIndex(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_wxfile_index

}