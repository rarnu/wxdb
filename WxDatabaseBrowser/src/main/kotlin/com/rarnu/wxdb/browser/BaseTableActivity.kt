package com.rarnu.wxdb.browser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.rarnu.kt.android.BaseAdapter
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.runOnMainThread
import com.rarnu.kt.android.showActionBack
import com.rarnu.wxdb.browser.database.DbIntf
import com.rarnu.wxdb.browser.database.FieldData
import com.rarnu.wxdb.browser.grid.WxGridAdapter
import com.rarnu.wxdb.browser.grid.WxGridView
import kotlinx.android.synthetic.main.activity_table.*
import kotlinx.android.synthetic.main.item_table.view.*
import kotlin.concurrent.thread

abstract class BaseTableActivity : Activity(), AdapterView.OnItemSelectedListener, WxGridAdapter.WxGridListener {

    protected lateinit var db: DbIntf
    private lateinit var grid: WxGridView
    private lateinit var adapterTableName: TableNameAdapter
    protected val listTableName = mutableListOf<FieldData>()
    protected var currentPage = 1
    protected var pageCount = 0
    protected var currentTableName = ""
    protected val currentTableField = mutableListOf<FieldData>()
    private val size = 50
    private val menuSQL = Menu.FIRST + 1

    abstract fun initDb(): DbIntf
    abstract fun titleResId(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)
        actionBar.title = resStr(titleResId())
        showActionBack()

        db = initDb()

        grid = WxGridView(this)
        grid.listener = this
        layTable.addView(grid)

        adapterTableName = TableNameAdapter(this, listTableName)
        spTable.adapter = adapterTableName
        spTable.onItemSelectedListener = this
        loadTableName()

        btnFirstPage.setOnClickListener {
            currentPage = 1
            queryTable()
        }
        btnLastPage.setOnClickListener {
            currentPage = if (pageCount == 0) 1 else pageCount
            queryTable()
        }
        btnPriorPage.setOnClickListener {
            currentPage--
            if (currentPage < 1) {
                currentPage = 1
            }
            queryTable()
        }
        btnNextPage.setOnClickListener {
            currentPage++
            if (currentPage > pageCount) {
                currentPage = pageCount
            }
            if (currentPage < 1) {
                currentPage = 1
            }
            queryTable()
        }
        btnReset.setOnClickListener {
            loadTableData(currentTableName)
            switchSQLMode(false)
        }
    }

    override fun onDestroy() {
        db.close()
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val mSQL = menu.add(0, menuSQL, 1, R.string.menu_sql)
        mSQL.setIcon(android.R.drawable.ic_menu_search)
        mSQL.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
            menuSQL -> {
                if (currentTableName != "") {
                    val inSQL = Intent(this, SQLActivity::class.java)
                    inSQL.putExtra("table", currentTableName)
                    val flist = mutableListOf<String>()
                    currentTableField.forEach { flist.add(it.str) }
                    inSQL.putExtra("field", flist.toTypedArray())
                    startActivityForResult(inSQL, 0)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val sql = data?.getStringExtra("sql")
            if (sql != null && sql != "") {
                executeSQL(sql)
            }
        }
    }

    class TableNameAdapter(ctx: Context, list: MutableList<FieldData>) : BaseAdapter<FieldData, TableNameAdapter.TableNameHolder>(ctx, list) {
        override fun fillHolder(baseVew: View, holder: TableNameHolder, item: FieldData, position: Int) {
            holder.tvTableName.text = if (item.isBlob) "[BLOB]" else item.str
        }

        override fun getAdapterLayout() = R.layout.item_table

        override fun getValueText(item: FieldData) = ""

        override fun newHolder(baseView: View) = TableNameHolder(baseView)

        inner class TableNameHolder(v: View) {
            val tvTableName = v.tvTableName!!
        }

    }

    private fun loadTableName() {
        val tmp = db.getTableList()
        listTableName.clear()
        tmp.forEach { listTableName.add(FieldData(it)) }
        adapterTableName.notifyDataSetChanged()

        // load first table
        if (listTableName.isNotEmpty()) {
            spTable.setSelection(0)
            val tableName = listTableName[0].str
            loadTableData(tableName)
        }
    }

    private fun loadTableData(tableName: String) {
        if (tableName == "") return
        switchSQLMode(false)
        currentPage = 1
        currentTableName = tableName
        db.getTableCount(tableName) { _, pc ->
            pageCount = pc
        }
        queryTable()
    }

    private fun queryTable() {
        switchButton(false)
        thread {
            val c = db.queryTable(currentTableName)
            if (c != null) {
                val tmp = cursorToCell(c)
                c.close()
                runOnMainThread {
                    grid.setItems(tmp)
                    tvPage.text = "$currentPage / $pageCount"
                    switchButton(true)
                }
            }
        }
    }

    private fun executeSQL(sql: String) {
        switchButton(false)
        thread {
            val c = db.executeSQL(sql)
            if (c != null) {
                val tmp = cursorToCellNoPage(c)
                c.close()
                runOnMainThread {
                    grid.setItems(tmp)
                    switchSQLMode(true)
                    switchButton(true)
                }
            }
        }
    }

    private fun switchButton(e: Boolean) {
        btnFirstPage.isEnabled = e
        btnPriorPage.isEnabled = e
        btnNextPage.isEnabled = e
        btnLastPage.isEnabled = e
        btnReset.isEnabled = e
    }


    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val tableName = listTableName[position]
        loadTableData(tableName.str)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun cursorToCell(c: Cursor): MutableList<List<FieldData>> {
        val result = mutableListOf<List<FieldData>>()
        val cnt = c.columnCount
        val start = (currentPage - 1) * size
        var idx = 0
        val head = mutableListOf<FieldData>()
        for (i in 0 until cnt) {
            head.add(FieldData(c.getColumnName(i)))
        }
        currentTableField.clear()
        currentTableField.addAll(head)
        result.add(head)
        c.moveToPosition(start - 1)
        while (c.moveToNext() && idx < size) {
            val line = mutableListOf<FieldData>()
            for (i in 0 until cnt) {
                line.add(if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) FieldData(c.getBlob(i)) else FieldData(c.getString(i) ?: ""))
            }
            result.add(line)
            idx++
        }
        return result
    }

    private fun cursorToCellNoPage(c: Cursor): MutableList<List<FieldData>> {
        val result = mutableListOf<List<FieldData>>()
        val cnt = c.columnCount
        val head = mutableListOf<FieldData>()
        for (i in 0 until cnt) {
            head.add(FieldData(c.getColumnName(i)))
        }
        result.add(head)
        c.moveToFirst()
        while (c.moveToNext()) {
            val line = mutableListOf<FieldData>()
            for (i in 0 until cnt) {
                line.add(if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) FieldData(c.getBlob(i)) else FieldData(c.getString(i) ?: ""))
            }
            result.add(line)
        }
        return result
    }

    override fun onWxGridClick(row: Int, col: Int, data: FieldData) {

    }

    private fun switchSQLMode(sqlMode: Boolean) {
        btnFirstPage.visibility = if (sqlMode) View.GONE else View.VISIBLE
        btnPriorPage.visibility = if (sqlMode) View.GONE else View.VISIBLE
        btnNextPage.visibility = if (sqlMode) View.GONE else View.VISIBLE
        btnLastPage.visibility = if (sqlMode) View.GONE else View.VISIBLE
        tvPage.visibility = if (sqlMode) View.GONE else View.VISIBLE
        btnReset.visibility = if (sqlMode) View.VISIBLE else View.GONE
    }
}