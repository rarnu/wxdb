package com.rarnu.wxdb.browser.dbui

import android.widget.AdapterView
import com.rarnu.kt.android.alert
import com.rarnu.kt.android.resStr
import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbIndexMicroMsg
import com.rarnu.wxdb.browser.database.DbSnsMicroMsg
import com.rarnu.wxdb.browser.database.FieldData
import com.rarnu.wxdb.browser.grid.WxGridAdapter
import com.rarnu.wxdb.browser.util.Alg

class IndexMicroMsgActivity : BaseTableActivity(), AdapterView.OnItemSelectedListener, WxGridAdapter.WxGridListener {

    override fun initDb() = DbIndexMicroMsg(Alg.getIndexMicroMsgPassword())

    override fun titleResId() = R.string.title_index

    override fun onWxGridClick(row: Int, col: Int, data: FieldData) {
        if (!data.isBlob && data.str.trim() != "") {
            alert("$currentTableName [$row, $col]", data.str, resStr(R.string.btn_ok)) { }
        }
    }

}