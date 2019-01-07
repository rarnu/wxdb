package com.rarnu.wxdb.browser.database

import com.rarnu.kt.android.runOnMainThread
import com.rarnu.wxdb.browser.util.Config
import com.rarnu.wxdb.browser.util.Alg
import com.rarnu.wxdb.browser.util.Utils
import java.io.File
import kotlin.concurrent.thread

object WxDatabasePrepare {

    private const val wxRootPath = "/data/data/com.tencent.mm/"
    private const val wxUinPath = wxRootPath + "shared_prefs/auth_info_key_prefs.xml"
    private const val wxAccountPath = wxRootPath + "shared_prefs/notify_key_pref_no_account.xml"
    private const val wxDbDir = wxRootPath + "MicroMsg/"
    private const val wxContactDb = "EnMicroMsg.db"
    private const val wxIndexDb = "FTS5IndexMicroMsg_encrypt.db"
    private const val wxSnsDb = "SnsMicroMsg.db"
    private const val wxDeviceInfoCfg = wxDbDir + "CompatibleInfo.cfg"

    fun refreshData(complete: () -> Unit) = thread {
        Utils.copyFile(wxUinPath, File(Config.basePath(), "uin.xml").absolutePath)
        Utils.copyFile(wxAccountPath, File(Config.basePath(), "account.xml").absolutePath)
        Utils.copyFile(wxDeviceInfoCfg, File(Config.basePath(), "device.cfg").absolutePath)
        val uin = Alg.getUin()
        val md5 = Utils.md5Encode("mm$uin")
        val contactPath = "$wxDbDir$md5/$wxContactDb"
        val indexDb = "$wxDbDir$md5/$wxIndexDb"
        val snsDb = "$wxDbDir$md5/$wxSnsDb"
        Utils.copyFile(contactPath, File(Config.basePath(), "msg.db").absolutePath)
        Utils.copyFile(indexDb, File(Config.basePath(), "index.db").absolutePath)
        Utils.copyFile(snsDb, File(Config.basePath(), "sns.db").absolutePath)

        runOnMainThread { complete() }
    }

}