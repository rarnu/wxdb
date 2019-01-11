package com.rarnu.wxdb.browser.database

import android.annotation.SuppressLint
import com.rarnu.kt.android.runOnMainThread
import com.rarnu.wxdb.browser.util.Config
import com.rarnu.wxdb.browser.util.Alg
import com.rarnu.wxdb.browser.util.Utils
import java.io.File
import kotlin.concurrent.thread

object WxDatabasePrepare {

    @SuppressLint("SdCardPath")
    private const val wxRootPath = "/data/data/com.tencent.mm/"
    private const val wxUinPath = wxRootPath + "shared_prefs/auth_info_key_prefs.xml"
    private const val wxAccountPath = wxRootPath + "shared_prefs/notify_key_pref_no_account.xml"
    private const val wxDbDir = wxRootPath + "MicroMsg/"
    private const val wxContactDb = "EnMicroMsg.db"
    private const val wxIndexDb = "FTS5IndexMicroMsg_encrypt.db"
    private const val wxSnsDb = "SnsMicroMsg.db"
    private const val wxAuxDb = "AuxData.db"
    private const val wxAppBrandComm = "AppBrandComm.db"
    private const val wxCommonOne = "CommonOneMicroMsg.db"
    private const val wxFavorite = "enFavorite.db"
    private const val wxPriority = "MicroMsgPriority.db"
    private const val wxStory = "StoryMicroMsg.db"
    private const val wxWxExpt = "WxExpt.db"
    private const val wxWxFileIndex = "WxFileIndex.db"
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
        val auxDb = "$wxDbDir$md5/$wxAuxDb"
        val appBrandCommDb = "$wxDbDir$md5/$wxAppBrandComm"
        val commonOneDb = "$wxDbDir$md5/$wxCommonOne"
        val favoriteDb = "$wxDbDir$md5/$wxFavorite"
        val priorityDb = "$wxDbDir$md5/$wxPriority"
        val storyDb = "$wxDbDir$md5/$wxStory"
        val wxExptDb = "$wxDbDir$md5/$wxWxExpt"
        val wxFileIndexDb = "$wxDbDir$md5/$wxWxFileIndex"
        Utils.copyFile(contactPath, File(Config.basePath(), "msg.db").absolutePath)
        Utils.copyFile(indexDb, File(Config.basePath(), "index.db").absolutePath)
        Utils.copyFile(snsDb, File(Config.basePath(), "sns.db").absolutePath)
        Utils.copyFile(auxDb, File(Config.basePath(), "aux.db").absolutePath)
        Utils.copyFile(appBrandCommDb, File(Config.basePath(), "appbrandcomm.db").absolutePath)
        Utils.copyFile(commonOneDb, File(Config.basePath(), "commonone.db").absolutePath)
        Utils.copyFile(favoriteDb, File(Config.basePath(), "favorite.db").absolutePath)
        Utils.copyFile(priorityDb, File(Config.basePath(), "priority.db").absolutePath)
        Utils.copyFile(storyDb, File(Config.basePath(), "story.db").absolutePath)
        Utils.copyFile(wxExptDb, File(Config.basePath(), "wxexpt.db").absolutePath)
        Utils.copyFile(wxFileIndexDb, File(Config.basePath(), "wxfileindex.db").absolutePath)

        runOnMainThread { complete() }
    }

}