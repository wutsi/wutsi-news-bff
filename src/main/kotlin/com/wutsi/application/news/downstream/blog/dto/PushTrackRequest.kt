package com.wutsi.application.news.downstream.blog.dto

data class PushTrackRequest(
    val time: Long = System.currentTimeMillis(),
    val duid: String? = null,
    val uid: String? = null,
    val hid: String? = null,
    val ua: String? = null,
    val ip: String? = null,
    val lat: Double? = null,
    val long: Double? = null,
    val referer: String? = null,
    val page: String? = null,
    val event: String? = null,
    val pid: String? = null,
    val value: String? = null,
    val url: String? = null
)
