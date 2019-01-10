package com.rarnu.wxdb.browser.dbui

import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbStory

class StoryActivity : BaseTableActivity() {

    override fun initDb() = DbStory()

    override fun titleResId() = R.string.title_story

}