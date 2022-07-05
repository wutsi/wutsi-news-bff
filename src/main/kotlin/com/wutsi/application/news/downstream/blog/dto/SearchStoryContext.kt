package com.wutsi.application.news.downstream.blog.dto

data class SearchStoryContext(
    val userId: Long? = null,
    val deviceId: String? = null,
    val deviceType: String? = null,
    val traffic: String? = null,
    val language: String? = null
)
