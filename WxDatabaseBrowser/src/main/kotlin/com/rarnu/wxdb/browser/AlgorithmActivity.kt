package com.rarnu.wxdb.browser

import android.os.Bundle
import br.tiagohm.markdownview.css.styles.Github
import com.rarnu.android.BackActivity
import com.rarnu.android.resStr
import kotlinx.android.synthetic.main.activity_algorithm.*

class AlgorithmActivity: BackActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_algorithm)
        actionBar?.title = resStr(R.string.title_algorithm)
        md.addStyleSheet(Github())
        md.loadMarkdownFromAsset("algorithm")
    }
}