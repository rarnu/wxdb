package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbCommonOne

class CommonOneActivity : BaseTableActivity() {

    override fun initDb() = DbCommonOne()

    override fun titleResId() = R.string.title_common_one

}