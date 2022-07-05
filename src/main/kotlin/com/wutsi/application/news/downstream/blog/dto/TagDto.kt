package com.wutsi.application.news.downstream.blog.dto

data class TagDto(
    val id: Long = -1,
    val name: String = "",
    val displayName: String = "",
    val totalStories: Long = 0
)
