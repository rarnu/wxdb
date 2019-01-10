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

    // db entry
    private lateinit var prefMicroMsg: Preference
    private lateinit var prefIndex: Preference
    private lateinit var prefSns: Preference
    private lateinit var prefAuxData: Preference
    private lateinit var prefCommonOne: Preference
    private lateinit var prefStory: Preference
    private lateinit var prefAppBrandComm: Preference
    private lateinit var prefFavorite: Preference
    private lateinit var prefPriority: Preference
    private lateinit var prefWxExpt: Preference
    private lateinit var prefWxFileIndex: Preference

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
        prefAuxData = pref(R.string.key_aux_data)
        prefCommonOne = pref(R.string.key_common_one)
        prefStory = pref(R.string.key_story)
        prefAppBrandComm = pref(R.string.key_app_brand_comm)
        prefFavorite = pref(R.string.key_favorite)
        prefPriority = pref(R.string.key_priority)
        prefWxExpt = pref(R.string.key_wxexpt)
        prefWxFileIndex = pref(R.string.key_wxfile_index)

        prefRefreshData.onPreferenceClickListener = this
        prefBaseInfo.onPreferenceClickListener = this
        prefAlgorithm.onPreferenceClickListener = this

        prefMicroMsg.onPreferenceClickListener = this
        prefIndex.onPreferenceClickListener = this
        prefSns.onPreferenceClickListener = this
        prefAuxData.onPreferenceClickListener = this
        prefCommonOne.onPreferenceClickListener = this
        prefStory.onPreferenceClickListener = this
        prefAppBrandComm.onPreferenceClickListener = this
        prefFavorite.onPreferenceClickListener = this
        prefPriority.onPreferenceClickListener = this
        prefWxExpt.onPreferenceClickListener = this
        prefWxFileIndex.onPreferenceClickListener = this

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
            resStr(R.string.key_micro_msg) -> showData("EnMicroMsg")                        // done
            resStr(R.string.key_index) -> showData("IndexMicroMsg")                         // done
            resStr(R.string.key_sns) -> showData("SnsMicroMsg")                             // done
            resStr(R.string.key_aux_data) -> showData("AuxData")                            // done
            resStr(R.string.key_common_one) -> showData("CommonOne")                        // done
            resStr(R.string.key_story) -> showData("Story")                                 // done
            resStr(R.string.key_app_brand_comm) -> showData("AppBrandComm")
            resStr(R.string.key_favorite) -> showData("Favorite")
            resStr(R.string.key_priority) -> showData("Priority")                           // done
            resStr(R.string.key_wxexpt) -> showData("WxExpt")
            resStr(R.string.key_wxfile_index) -> showData("WxFileIndex")
        }
        return true
    }

    private fun showData(name: String) {
        val clz = Class.forName("$packageName.dbui.${name}Activity")
        val inTable = Intent(this, clz)
        startActivity(inTable)
    }
}


