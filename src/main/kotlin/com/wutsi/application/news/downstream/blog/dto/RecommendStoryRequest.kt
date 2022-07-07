package com.wutsi.application.news.downstream.blog.dto

data class RecommendStoryRequest(
    val storyId: Long? = null,
    val limit: Int = 20,
    val context: SearchStoryContext = SearchStoryContext()
)
