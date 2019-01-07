package com.rarnu.wxdb.browser.sns

import java.io.Serializable

class SnsInfo: Serializable {

    var id = ""
    var authorName: String? = ""
    var content: String? = ""
    var authorId: String? = ""

    val likes = mutableListOf<Like>()
    val comments = mutableListOf<Comment>()
    val mediaList = mutableListOf<String>()

    var rawXML: String? = ""
    var timestamp = 0L
    var ready = false
    var isCurrentUser = false

    class Like: Serializable {
        var userName: String? = ""
        var userId: String? = ""
        var isCurrentUser = false
    }

    class Comment: Serializable {
        var authorName: String? = ""
        var content: String? = ""
        var toUser: String? = ""
        var authorId: String? = ""
        var toUserId: String? = ""
        var isCurrentUser = false
    }

}
