package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbIndexMicroMsg
import com.rarnu.wxdb.browser.util.Alg

class IndexMicroMsgActivity : BaseTableActivity() {

    override fun initDb() = DbIndexMicroMsg(Alg.getIndexMicroMsgPassword())

    override fun titleResId() = R.string.title_index

}