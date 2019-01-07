package com.rarnu.wxdb.browser.sns

import android.util.Log
import com.rarnu.wxdb.browser.ref.WxClassLoader
import java.util.*
import java.util.regex.Pattern

class Parser {

    @Throws(Throwable::class)
    fun parseSnsAllFromBin(snsDetailBin: ByteArray?, snsObjectBin: ByteArray?): SnsInfo? {
        val snsDetail = parseSnsDetailFromBin(snsDetailBin)
        val snsObject = parseSnsObjectFromBin(snsObjectBin)
        val snsInfo = parseSnsDetail(snsDetail)
        parseSnsObject(snsObject, snsInfo)
        return snsInfo
    }

    @Throws(Throwable::class)
    fun parseSnsDetailFromBin(bin: ByteArray?): Any? {
        val snsDetail = WxClassLoader.snsDetail?.newInstance()
        val fromBinMethod = WxClassLoader.snsDetail?.getMethod("am", ByteArray::class.java)
        fromBinMethod?.invoke(snsDetail, bin)
        return snsDetail
    }

    @Throws(Throwable::class)
    fun parseSnsDetail(snsDetail: Any?): SnsInfo? {
        val snsDetailParserMethod = WxClassLoader.snsDetailParser?.getMethod("a", WxClassLoader.snsDetail)
        val xmlResult = snsDetailParserMethod?.invoke(this, snsDetail) as? String
        Log.e("DB", "xml => $xmlResult")
        return parseTimelineXML(xmlResult)
    }

    @Throws(Throwable::class)
    fun parseSnsObjectFromBin(bin: ByteArray?): Any? {
        val snsObject = WxClassLoader.snsObject?.newInstance()
        val fromBinMethod = WxClassLoader.snsObject?.getMethod("am", ByteArray::class.java)
        fromBinMethod?.invoke(snsObject, bin)
        return snsObject
    }

    companion object {

        @Throws(Throwable::class)
        fun parseTimelineXML(xmlResult: String?): SnsInfo? {
            val currentSns = SnsInfo()
            val userIdPattern = Pattern.compile("<username><!\\[CDATA\\[(.+?)\\]\\]></username>", Pattern.DOTALL)
            val contentPattern = Pattern.compile("<contentDesc><!\\[CDATA\\[(.+?)\\]\\]></contentDesc>", Pattern.DOTALL)
            // val mediaPattern = Pattern.compile("<media>.*?<url.*?><!\\[CDATA\\[(.+?)\\]\\]></url>.*?</media>", Pattern.DOTALL)
            val mediaIdPattern = Pattern.compile("<media>.*?<id><!\\[CDATA\\[(.+?)\\]\\]></id>.*?</media>", Pattern.DOTALL)
            val timestampPattern = Pattern.compile("<createTime><!\\[CDATA\\[(.+?)\\]\\]></createTime>")

            val userIdMatcher = userIdPattern.matcher(xmlResult)
            val contentMatcher = contentPattern.matcher(xmlResult)
            val mediaMatcher = mediaIdPattern.matcher(xmlResult)
            val timestampMatcher = timestampPattern.matcher(xmlResult)

            currentSns.id = getTimelineId(xmlResult)
            currentSns.rawXML = xmlResult
            if (timestampMatcher.find()) {
                currentSns.timestamp = timestampMatcher.group(1).toLong()
            }
            if (userIdMatcher.find()) {
                currentSns.authorId = userIdMatcher.group(1)
            }
            if (contentMatcher.find()) {
                currentSns.content = contentMatcher.group(1)
            }

            while (mediaMatcher.find()) {
                var flag = true
                for (i in 0 until currentSns.mediaList.size) {
                    if (currentSns.mediaList[i] == mediaMatcher.group(1)) {
                        flag = false
                        break
                    }
                }
                if (flag) {
                    currentSns.mediaList.add(mediaMatcher.group(1))
                }
            }

            return currentSns
        }

        @Throws(Throwable::class)
        fun parseSnsObject(aqiObject: Any?, matchSns: SnsInfo?) {

            var field = aqiObject?.javaClass?.getField("iYA")
            val userId = field?.get(aqiObject)
            field = aqiObject?.javaClass?.getField("jyd")
            val nickname = field?.get(aqiObject)

            if (userId == null || nickname == null) {
                return
            }

            matchSns?.ready = true
            matchSns?.authorName = nickname as? String

            field = aqiObject?.javaClass?.getField("jJX")
            val list = field?.get(aqiObject) as? LinkedList<*>
            if (list != null) {
                for (i in 0 until list.size) {
                    val childObject = list[i]
                    parseSnsObjectExt(childObject, true, matchSns)
                }
            }

            field = aqiObject?.javaClass?.getField("jJU")
            val likeList = field?.get(aqiObject) as? LinkedList<*>

            if (likeList != null) {
                for (i in 0 until likeList.size) {
                    val likeObject = likeList[i]
                    parseSnsObjectExt(likeObject, false, matchSns)
                }
            }
        }

        @Throws(Throwable::class)
        fun parseSnsObjectExt(apzObject: Any?, isComment: Boolean, matchSns: SnsInfo?) {
            if (isComment) {
                var field = apzObject?.javaClass?.getField("jyd")
                val authorName = field?.get(apzObject)
                field = apzObject?.javaClass?.getField("jJM")
                val replyToUserId = field?.get(apzObject)
                field = apzObject?.javaClass?.getField("fsI")
                val commentContent = field?.get(apzObject)
                field = apzObject?.javaClass?.getField("iYA")
                val authorId = field?.get(apzObject)

                if (authorId == null || commentContent == null || authorName == null) {
                    return
                }
                if (matchSns != null) {
                    for (i in 0 until matchSns.comments.size) {
                        val loadedComment = matchSns.comments[i]
                        if (loadedComment.authorId == (authorId as? String) && loadedComment.content == (commentContent as? String)) {
                            return
                        }
                    }
                }

                val newComment = SnsInfo.Comment()
                newComment.authorName = authorName as? String
                newComment.content = commentContent as? String
                newComment.authorId = authorId as? String
                newComment.toUserId = replyToUserId as? String

                if (matchSns != null) {
                    for (i in 0 until matchSns.comments.size) {
                        val loadedComment = matchSns.comments[i]
                        if (replyToUserId != null && loadedComment.authorId == (replyToUserId as? String)) {
                            newComment.toUser = loadedComment.authorName
                            break
                        }
                    }
                }

                matchSns?.comments?.add(newComment)
            } else {
                var field = apzObject?.javaClass?.getField("jyd")
                val nickname = field?.get(apzObject)
                field = apzObject?.javaClass?.getField("iYA")
                val userId = field?.get(apzObject)
                if (nickname == null || userId == null) {
                    return
                }

                if ((userId as? String) == "") {
                    return
                }
                if (matchSns != null) {
                    for (i in 0 until matchSns.likes.size) {
                        if (matchSns.likes[i].userId == (userId as? String)) {
                            return
                        }
                    }
                }

                val newLike = SnsInfo.Like()
                newLike.userId = userId as? String
                newLike.userName = nickname as? String
                matchSns?.likes?.add(newLike)
            }
        }

        fun getTimelineId(xmlResult: String?): String {
            val idPattern = Pattern.compile("<id><!\\[CDATA\\[(.+?)\\]\\]></id>")
            val idMatcher = idPattern.matcher(xmlResult)
            return if (idMatcher.find()) { idMatcher.group(1) } else { "" }
        }

    }
}