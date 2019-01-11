package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbFavorite
import com.rarnu.wxdb.browser.util.Alg

class FavoriteActivity : BaseTableActivity() {

    override fun initDb() = DbFavorite(Alg.getEnMicroMsgPassword())

    override fun titleResId() = R.string.title_favorite

}