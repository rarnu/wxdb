package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbPriority
import com.rarnu.wxdb.browser.util.Alg

class PriorityActivity : BaseTableActivity() {

    override fun initDb() = DbPriority(Alg.getPriorityPassword())

    override fun titleResId() = R.string.title_priority

}