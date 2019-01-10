package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbWxExpt
import com.rarnu.wxdb.browser.util.Alg

class WxExptActivity : BaseTableActivity() {

    override fun initDb() = DbWxExpt(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_wxexpt

}