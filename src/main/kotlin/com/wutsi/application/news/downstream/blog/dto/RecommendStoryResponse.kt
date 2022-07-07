package com.wutsi.application.news.downstream.blog.dto

data class RecommendStoryResponse(
    val stories: List<StorySummaryDto> = emptyList()
)
