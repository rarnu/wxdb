package com.rarnu.wxdb.browser

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.preference.Preference
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.rarnu.kt.android.*
import com.rarnu.wxdb.browser.database.WxDatabasePrepare
import com.rarnu.wxdb.browser.ref.WxClassLoader
import com.rarnu.wxdb.browser.util.Config
import com.rarnu.wxdb.browser.util.Utils
import net.sqlcipher.database.SQLiteDatabase

class MainActivity : PreferenceActivity(), Preference.OnPreferenceClickListener {

    private var permissionGranted = false
    private var rooted = false

    private lateinit var prefRefreshData: Preference
    private lateinit var prefBaseInfo: Preference
    private lateinit var prefAlgorithm: Preference
    private lateinit var prefMicroMsg: Preference
    private lateinit var prefIndex: Preference
    private lateinit var prefSns: Preference

    private val menuInfo = Menu.FIRST + 9

    private val keyFirstStart = "key_first_start"

    override fun onCreate(savedInstanceState: Bundle?) {
        initUI()
        super.onCreate(savedInstanceState)
        SQLiteDatabase.loadLibs(this)
    }

    override fun getPreferenceXml() = R.xml.main

    override fun onPreparedPreference() {
        prefRefreshData = pref(R.string.key_refresh_data)
        prefBaseInfo = pref(R.string.key_base_info)
        prefAlgorithm = pref(R.string.key_algorithm)
        prefMicroMsg = pref(R.string.key_micro_msg)
        prefIndex= pref(R.string.key_index)
        prefSns= pref(R.string.key_sns)

        prefRefreshData.onPreferenceClickListener = this
        prefBaseInfo.onPreferenceClickListener = this
        prefAlgorithm.onPreferenceClickListener = this
        prefMicroMsg.onPreferenceClickListener = this
        prefIndex.onPreferenceClickListener = this
        prefSns.onPreferenceClickListener = this

        switchState(false)

        permissionGranted = checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        if (!permissionGranted) {
            requestPermissions(arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        } else {
            checkRootPermission()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>?, grantResults: IntArray?) {
        if (grantResults != null && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                permissionGranted = true
                checkRootPermission()
            }
        }
    }

    private fun checkRootPermission() {
        Config.init()
        rooted = Utils.hasRoot()
        if (!rooted) {
            toast(resStr(R.string.toast_no_root))
        } else {
            WxClassLoader.initClasses(this) {
                if (readConfig(keyFirstStart, true)) {
                    Log.e("DB", "keyFirstStart")
                    WxDatabasePrepare.refreshData {
                        writeConfig(keyFirstStart, false)
                        switchState(true)
                    }
                } else {
                    switchState(true)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        val mInfo = menu.add(0, menuInfo, 0, R.string.menu_info)
        mInfo.setIcon(android.R.drawable.ic_menu_info_details)
        mInfo.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            menuInfo -> startActivity(Intent(this, AboutActivity::class.java))
        }
        return true
    }

    private fun switchState(e: Boolean) {
        prefRefreshData.isEnabled = e
        prefBaseInfo.isEnabled = e
        prefAlgorithm.isEnabled = e
        prefMicroMsg.isEnabled = e
        prefIndex.isEnabled = e
        prefSns.isEnabled = e
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when(preference.key) {
            resStr(R.string.key_refresh_data) -> {
                preference.isEnabled = false
                preference.summary = resStr(R.string.summary_in_processing)
                WxDatabasePrepare.refreshData {
                    preference.summary = ""
                    preference.isEnabled = true
                }
            }
            resStr(R.string.key_base_info) -> startActivity(Intent(this, BaseInfoActivity::class.java))
            resStr(R.string.key_algorithm) -> startActivity(Intent(this, AlgorithmActivity::class.java))
            resStr(R.string.key_micro_msg) -> showData(0)
            resStr(R.string.key_index) -> showData(1)
            resStr(R.string.key_sns) -> showData(2)
        }
        return true
    }

    private fun showData(i: Int) {
        val inData = Intent(this, TableActivity::class.java)
        inData.putExtra("type", i)
        startActivity(inData)
    }


}


