package com.rarnu.wxdb.browser

import android.app.Activity
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_algorithm.*
import br.tiagohm.markdownview.css.styles.Github
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.showActionBack

class AlgorithmActivity: Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algorithm)
        actionBar.title = resStr(R.string.title_algorithm)
        showActionBack()
        md.addStyleSheet(Github())
        md.loadMarkdownFromAsset("algorithm")
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

}