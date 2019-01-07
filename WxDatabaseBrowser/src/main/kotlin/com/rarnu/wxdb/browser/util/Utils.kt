package com.rarnu.wxdb.browser.util

import com.rarnu.kt.android.runCommand
import java.io.File
import java.security.MessageDigest

object Utils {

    fun hasRoot(): Boolean {
        var ret = false
        runCommand {
            runAsRoot = true
            commands.add("mount")
            result { _, error ->
                if (error.trim() == "") {
                    ret = true
                }
            }
        }
        return ret
    }

    fun readFile(path: String) = readFile(File(path))

    fun readFile(f: File): String {
        var ret = ""
        runCommand {
            runAsRoot = true
            commands.add("cat ${f.absolutePath}")
            result { output, _ ->
                ret = output
            }
        }
        return ret.trim()
    }

    fun writeFile(path: String, text: String) = writeFile(File(path), text)

    fun writeFile(f: File, text: String) {
        runCommand {
            runAsRoot = true
            commands.add("echo \"${text.trim()}\" > \"${f.absolutePath}\"")
        }
    }

    fun mkdir(p: String) = mkdir(File(p))

    fun mkdir(f: File) {
        runCommand {
            runAsRoot = true
            commands.add("mkdir -p ${f.absolutePath}")
        }
    }

    fun deleteFile(path: String) = deleteFile(File(path))

    fun deleteFile(f: File) {
        runCommand {
            runAsRoot = true
            commands.add("rm -f ${f.absolutePath}")
        }
    }

    fun deleteFolder(path: String) = deleteFolder(File(path))

    fun deleteFolder(f: File) {
        runCommand {
            runAsRoot = true
            commands.add("rm -fr ${f.absolutePath}")
        }
    }

    fun copyFile(src: String, dest: String) {
        runCommand {
            runAsRoot = true
            commands.add("cp $src $dest")
        }
    }

    fun md5Encode(inStr: String): String {
        val md5 = MessageDigest.getInstance("MD5")
        val byteArray = inStr.toByteArray()
        val md5Bytes = md5.digest(byteArray)
        var hexValue = ""
        for (b in md5Bytes) {
            val v = b.toInt() and 0xff
            if (v < 16) hexValue += "0"
            hexValue += Integer.toHexString(v)
        }
        return hexValue
    }

}