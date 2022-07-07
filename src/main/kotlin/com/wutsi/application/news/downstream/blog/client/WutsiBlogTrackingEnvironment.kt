package com.wutsi.application.news.downstream.blog.client

enum class WutsiBlogTrackingEnvironment(
    val url: String,
) {
    PRODUCTION("https://wutsi-tracking-prod.herokuapp.com"),
    SANDBOX("https://wutsi-tracking-test.herokuapp.com")
}
