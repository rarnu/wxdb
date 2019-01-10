package com.rarnu.wxdb.browser

import android.os.Bundle
import android.preference.Preference
import android.view.MenuItem
import com.rarnu.kt.android.PreferenceActivity
import com.rarnu.kt.android.resStr
import com.rarnu.kt.android.showActionBack
import com.rarnu.wxdb.browser.util.Alg

class BaseInfoActivity: PreferenceActivity() {

    private lateinit var prefUin: Preference
    private lateinit var prefDid: Preference
    private lateinit var prefWxid: Preference
    private lateinit var prefEnPwd: Preference
    private lateinit var prefIndexPwd: Preference
    private lateinit var prefPriorityPwd: Preference
    private lateinit var prefUserFolder: Preference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        actionBar.title = resStr(R.string.title_base_info)
        showActionBack()
    }

    override fun getPreferenceXml() = R.xml.baseinfo

    override fun onPreparedPreference() {
        prefUin = pref(R.string.key_uin)
        prefDid = pref(R.string.key_did)
        prefWxid = pref(R.string.key_wxid)
        prefEnPwd = pref(R.string.key_en_pwd)
        prefIndexPwd = pref(R.string.key_index_pwd)
        prefPriorityPwd = pref(R.string.key_priority_pwd)
        prefUserFolder = pref(R.string.key_user_folder)
        prefUin.summary = Alg.getUin()
        prefDid.summary = Alg.loadDeviceId()
        prefWxid.summary = Alg.getLoginAccount()
        prefEnPwd.summary = Alg.getEnMicroMsgPassword()
        prefIndexPwd.summary = Alg.getIndexMicroMsgPassword()
        prefPriorityPwd.summary = Alg.getPriorityPassword()
        prefUserFolder.summary = Alg.getUserFolder()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }



}