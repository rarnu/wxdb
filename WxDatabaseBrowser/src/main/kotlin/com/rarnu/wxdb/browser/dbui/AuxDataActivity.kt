package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbAuxData

class AuxDataActivity : BaseTableActivity() {

    override fun initDb() = DbAuxData()

    override fun titleResId() = R.string.title_aux_data

}