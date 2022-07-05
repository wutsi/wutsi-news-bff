package com.wutsi.application.news.config

enum class WutsiBlogEnvironment(
    val url: String,
) {
    PRODUCTION("https://com-wutsi-blog.herokuapp.com"),
    SANDBOX("https://int-com-wutsi-blog.herokuapp.com")
}
