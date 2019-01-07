package com.rarnu.wxdb.browser.util

import android.os.Environment
import java.io.File

object Config {

    private const val PATH_CONFIG = ".wxdb"
    private lateinit var BASE_PATH: File

    fun init() {
        BASE_PATH = File(Environment.getExternalStorageDirectory(), PATH_CONFIG)
        if (!BASE_PATH.exists()) {
            Utils.mkdir(BASE_PATH)
        }
    }

    fun basePath() = BASE_PATH

}