package com.rarnu.wxdb.browser

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import com.rarnu.kt.android.*
import com.rarnu.wxdb.browser.database.DbEnMicroMsg
import com.rarnu.wxdb.browser.database.DbIndexMicroMsg
import com.rarnu.wxdb.browser.database.DbIntf
import com.rarnu.wxdb.browser.database.DbSnsMicroMsg
import com.rarnu.wxdb.browser.grid.WxGridAdapter
import com.rarnu.wxdb.browser.grid.WxGridView
import com.rarnu.wxdb.browser.util.Alg
import kotlinx.android.synthetic.main.activity_table.*
import kotlinx.android.synthetic.main.item_table.view.*
import java.io.File
import kotlin.concurrent.thread

class TableActivity : Activity(), AdapterView.OnItemSelectedListener, WxGridAdapter.WxGridListener {

    private var dbType = -1
    private lateinit var db: DbIntf
    private lateinit var grid: WxGridView
    private lateinit var adapterTableName: TableNameAdapter
    private val listTableName = mutableListOf<String>()
    private var currentPage = 1
    private var pageCount = 0
    private var currentTableName = ""
    private val currentTableField = mutableListOf<String>()
    private val size = 50
    private val menuSQL = Menu.FIRST + 1
    private val menuSns = Menu.FIRST + 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_table)

        dbType = intent.getIntExtra("type", 0)
        actionBar.title = resStr(when (dbType) {
            1 -> R.string.title_index
            2 -> R.string.title_sns
            else -> R.string.title_micro_msg
        })
        showActionBack()

        db = when (dbType) {
            1 -> DbIndexMicroMsg(Alg.getIndexMicroMsgPassword())
            2 -> DbSnsMicroMsg()
            else -> DbEnMicroMsg(Alg.getEnMicroMsgPassword())
        }

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
        menu.clear()
        if (dbType == 2) {
            val mSns = menu.add(0, menuSns, 0, R.string.menu_sns)
            mSns.setIcon(android.R.drawable.ic_menu_gallery)
            mSns.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        }
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
                    inSQL.putExtra("field", currentTableField.toTypedArray())
                    startActivityForResult(inSQL, 0)
                }
            }
            menuSns -> startActivity(Intent(this, SnsActivity::class.java))
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            val sql = data?.getStringExtra("sql")
            if (sql != null && sql != "") {
                executeSQL(sql)
            }
        }
    }

    class TableNameAdapter(ctx: Context, list: MutableList<String>) : BaseAdapter<String, TableNameAdapter.TableNameHolder>(ctx, list) {
        override fun fillHolder(baseVew: View, holder: TableNameHolder, item: String, position: Int) {
            holder.tvTableName.text = item
        }

        override fun getAdapterLayout() = R.layout.item_table

        override fun getValueText(item: String) = ""

        override fun newHolder(baseView: View) = TableNameHolder(baseView)

        inner class TableNameHolder(v: View) {
            val tvTableName = v.tvTableName!!
        }

    }

    private fun loadTableName() {
        val tmp = db.getTableList()
        listTableName.clear()
        listTableName.addAll(tmp)
        adapterTableName.notifyDataSetChanged()

        // load first table
        if (listTableName.isNotEmpty()) {
            spTable.setSelection(0)
            val tableName = listTableName[0]
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
        loadTableData(tableName)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    private fun cursorToCell(c: Cursor): MutableList<List<String>> {
        val result = mutableListOf<List<String>>()
        val cnt = c.columnCount
        val start = (currentPage - 1) * size
        var idx = 0
        val head = mutableListOf<String>()
        for (i in 0 until cnt) {
            head.add(c.getColumnName(i))
        }
        currentTableField.clear()
        currentTableField.addAll(head)
        result.add(head)
        c.moveToPosition(start - 1)
        while (c.moveToNext() && idx < size) {
            val line = mutableListOf<String>()
            for (i in 0 until cnt) {
                line.add(if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) "[BLOB]" else c.getString(i) ?: "")
            }
            result.add(line)
            idx++
        }
        return result
    }

    private fun cursorToCellNoPage(c: Cursor): MutableList<List<String>> {
        val result = mutableListOf<List<String>>()
        val cnt = c.columnCount
        val head = mutableListOf<String>()
        for (i in 0 until cnt) {
            head.add(c.getColumnName(i))
        }
        result.add(head)
        c.moveToFirst()
        while (c.moveToNext()) {
            val line = mutableListOf<String>()
            for (i in 0 until cnt) {
                line.add(if (c.getType(i) == Cursor.FIELD_TYPE_BLOB) "[BLOB]" else c.getString(i) ?: "")
            }
            result.add(line)
        }
        return result
    }

    override fun onWxGridClick(row: Int, col: Int, isBlob: Boolean, text: String) {
        if (!isBlob && text.trim() != "") {
            // find file md5
            findFile(text) {
                if (it == null) {
                    alert("$currentTableName [$row, $col]", text, resStr(R.string.btn_ok)) { }
                } else {
                    val bmp = try {
                        BitmapFactory.decodeFile(it.absolutePath)
                    } catch (e: Throwable) {
                        null
                    }
                    if (bmp != null) {
                        val iv = ImageView(this)
                        iv.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                        iv.setImageBitmap(bmp)
                        AlertDialog.Builder(this).setTitle("$currentTableName [$row, $col]").setMessage(text)
                                .setView(iv).setPositiveButton(R.string.btn_ok, null).show()
                    } else {
                        alert("$currentTableName [$row, $col]", text, resStr(R.string.btn_ok)) { }
                    }
                }
            }
        }
    }

    private fun findFile(fn: String, callback: (f: File?) -> Unit) = thread {
        var img: File? = null
        var imgPath = fn
        imgPath = imgPath.replace("THUMBNAIL_DIRPATH://", "")
        imgPath = imgPath.replace("\n", "").replace("\r", "").replace("\"", "").trim()

        runCommand {
            runAsRoot = true
            commands.add("find /sdcard/tencent/MicroMsg -name \"$imgPath\"")
            result { output, _ ->
                val paths = output.trim().split("\n")
                for (s in paths) {
                    if (s.contains("temp")) continue
                    var path = s
                    if (path.contains("emoji")) {
                        path += "_cover"
                    }
                    if (File(path + "hd").exists()) {
                        path += "hd"
                    }
                    val tmpImg = File(path)
                    if (tmpImg.exists()) {
                        img = tmpImg
                        break
                    }
                }
                runOnMainThread { callback(img) }
            }
        }
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