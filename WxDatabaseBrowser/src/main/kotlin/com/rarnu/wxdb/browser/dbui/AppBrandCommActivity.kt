package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbAppBrandComm
import com.rarnu.wxdb.browser.util.Alg

class AppBrandCommActivity : BaseTableActivity() {

    override fun initDb() = DbAppBrandComm(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_app_brand_comm

}