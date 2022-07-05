package com.wutsi.application.news.downstream.blog.dto

data class TopicDto(
    val id: Long = -1,
    val parentId: Long = -1,
    val name: String = ""
)
