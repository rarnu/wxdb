package com.rarnu.wxdb.browser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.rarnu.kt.android.BaseAdapter
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.showActionBack
import kotlinx.android.synthetic.main.activity_sql.*
import kotlinx.android.synthetic.main.item_field_name.view.*

class SQLActivity: Activity() {

    private var tableName = ""
    private val tableFields = mutableListOf<String>()
    private lateinit var adapter: FieldNameAdapter

    private val menuRun = Menu.FIRST + 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sql)
        actionBar.title = resStr(R.string.menu_sql)
        showActionBack()
        tableName = intent.getStringExtra("table")
        tableFields.addAll(intent.getStringArrayExtra("field"))
        tvSelect.text = "SELECT * FROM $tableName WHERE"

        adapter = FieldNameAdapter(this, tableFields)
        lvField.adapter = adapter
        spField.adapter = adapter

        val lstOrder = mutableListOf("ASC", "DESC")
        val adapterOrder = FieldNameAdapter(this, lstOrder)
        spOrder.adapter = adapterOrder

        lvField.setOnItemClickListener { _, _, position, _ -> etSQLWhere.text.insert(etSQLWhere.selectionStart, tableFields[position] + " ") }
        btnSQLAnd.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "AND ") }
        btnSQLOr.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "OR ") }
        btnSQLBrackets.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "(  ) ") }
        btnSQLQuote.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "'' ") }
        btnSQLEqual.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "= ") }
        btnSQLLT.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "< ") }
        btnSQLGT.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "> ") }
        btnSQLPercent.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "%") }
        btnSQLBet.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "BETWEEN ") }
        btnSQLIn.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "IN ") }
        btnSQLNotIn.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "NOT IN ") }
        btnSQLLike.setOnClickListener { etSQLWhere.text.insert(etSQLWhere.selectionStart, "LIKE ") }

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        val mRun = menu.add(0, menuRun, 0, R.string.menu_exec_sql)
        mRun.setIcon(android.R.drawable.ic_menu_send)
        mRun.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            menuRun -> {
                val inRet = Intent()
                inRet.putExtra("sql", buildSQL())
                setResult(RESULT_OK, inRet)
                finish()
            }
        }
        return true
    }

    private fun buildSQL(): String {
        var sql = "SELECT * FROM $tableName"
        val where = etSQLWhere.text.toString().trim()
        if (where != "") {
            sql += " WHERE $where"
        }
        if (chkOrderBy.isChecked) {
            sql += " ORDER BY ${tableFields[spField.selectedItemPosition]} ${if (spOrder.selectedItemPosition == 0) "ASC" else "DESC"}"
        }
        return sql
    }

    class FieldNameAdapter(ctx: Context, list: MutableList<String>) : BaseAdapter<String, FieldNameAdapter.FieldNameHolder>(ctx, list) {

        override fun fillHolder(baseVew: View, holder: FieldNameHolder, item: String, position: Int) {
            holder.tvFieldName.text = item
        }
        override fun getAdapterLayout() = R.layout.item_field_name

        override fun getValueText(item: String) = ""

        override fun newHolder(baseView: View) = FieldNameHolder(baseView)

        inner class FieldNameHolder(v: View) {
            val tvFieldName = v.tvFieldName
        }
    }

}