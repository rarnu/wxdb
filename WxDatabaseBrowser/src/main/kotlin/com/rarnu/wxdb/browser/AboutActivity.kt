package com.rarnu.wxdb.browser

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import br.tiagohm.markdownview.css.styles.Github
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.showActionBack
import kotlinx.android.synthetic.main.activity_algorithm.*

class AboutActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        actionBar.title = resStr(R.string.title_info)
        showActionBack()
        md.addStyleSheet(Github())
        md.loadMarkdownFromAsset("about")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}