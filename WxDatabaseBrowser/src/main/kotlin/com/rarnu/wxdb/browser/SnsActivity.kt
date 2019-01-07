package com.rarnu.wxdb.browser

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import com.rarnu.kt.android.BaseAdapter
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.runOnMainThread
import com.rarnu.kt.android.showActionBack
import com.rarnu.wxdb.browser.sns.Sns
import com.rarnu.wxdb.browser.sns.SnsInfo
import kotlinx.android.synthetic.main.acticity_sns.*
import kotlinx.android.synthetic.main.item_sns.view.*
import kotlin.concurrent.thread

class SnsActivity: Activity(), AdapterView.OnItemClickListener {


    private val size = 30
    private val listAll = mutableListOf<SnsInfo>()
    private val list = mutableListOf<SnsInfo>()
    private var currentPage = 1
    private var pageCount = 0
    private lateinit var adapter: SnsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.acticity_sns)
        actionBar.title = resStr(R.string.menu_sns)
        showActionBack()

        adapter = SnsAdapter(this, list)
        lvSns.adapter = adapter
        lvSns.onItemClickListener = this

        btnFirstPage.setOnClickListener {
            currentPage = 1
            showSnsData()
        }
        btnLastPage.setOnClickListener {
            currentPage = if (pageCount == 0) 1 else pageCount
            showSnsData()
        }
        btnPriorPage.setOnClickListener {
            currentPage--
            if (currentPage < 1) {
                currentPage = 1
            }
            showSnsData()
        }
        btnNextPage.setOnClickListener {
            currentPage++
            if (currentPage > pageCount) {
                currentPage = pageCount
            }
            if (currentPage < 1) {
                currentPage = 1
            }
            showSnsData()
        }

        decodeSnsData()
    }

    private fun decodeSnsData() = thread {
        val sns = Sns()
        try {
            sns.run()
            val tmp = sns.snsList
            listAll.addAll(tmp)
            Log.e("DB", "recordCount => ${listAll.size}")
            pageCount = listAll.size / size
            if (listAll.size % size != 0) {
                pageCount++
            }
        } catch (e: Throwable) {
        }
        runOnMainThread { showSnsData() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun showSnsData() {
        tvPage.text = "$currentPage / $pageCount"
        val start = (currentPage - 1) * size
        list.clear()
        for (i in start until start + size) {
            if (i < listAll.size) {
                list.add(listAll[i])
            }
        }
        adapter.notifyDataSetChanged()
    }

    class SnsAdapter(ctx: Context, list: MutableList<SnsInfo>) : BaseAdapter<SnsInfo, SnsAdapter.SnsHolder>(ctx, list) {
        override fun fillHolder(baseVew: View, holder: SnsHolder, item: SnsInfo, position: Int) {
            holder.tvAuthor.text = item.authorName
            holder.tvLikeCount.text = item.likes.size.toString()
            holder.tvCommentCount.text = item.comments.size.toString()
            holder.tvSnsContent.text = item.content
        }

        override fun getAdapterLayout() = R.layout.item_sns

        override fun getValueText(item: SnsInfo) = ""

        override fun newHolder(baseView: View) = SnsHolder(baseView)

        inner class SnsHolder(v: View) {
            val tvSnsContent = v.tvSnsContent
            val tvCommentCount = v.tvCommentCount
            val tvLikeCount = v.tvLikeCount
            val tvAuthor = v.tvAuthor
        }

    }

    override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val info = list[position]
        val inDetail = Intent(this, SnsDetailActivity::class.java)
        inDetail.putExtra("info", info)
        startActivity(inDetail)
    }

}