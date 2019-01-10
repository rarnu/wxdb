package com.rarnu.wxdb.browser.dbui

import android.widget.AdapterView
import com.rarnu.kt.android.alert
import com.rarnu.kt.android.resStr
import com.rarnu.wxdb.browser.BaseTableActivity
import com.rarnu.wxdb.browser.R
import com.rarnu.wxdb.browser.database.DbSnsMicroMsg
import com.rarnu.wxdb.browser.database.FieldData
import com.rarnu.wxdb.browser.grid.WxGridAdapter

class SnsMicroMsgActivity : BaseTableActivity(), AdapterView.OnItemSelectedListener, WxGridAdapter.WxGridListener {

    override fun initDb() = DbSnsMicroMsg()

    override fun titleResId() = R.string.title_sns

    override fun onWxGridClick(row: Int, col: Int, data: FieldData) {

        if (!data.isBlob && data.str.trim() != "") {
            alert("$currentTableName [$row, $col]", data.str, resStr(R.string.btn_ok)) { }
        } else if (data.isBlob && data.blob != null) {
            // TODO: blob data
        }
    }


}