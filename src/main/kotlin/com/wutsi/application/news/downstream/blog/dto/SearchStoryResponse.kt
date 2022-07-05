package com.wutsi.application.news.downstream.blog.dto

data class SearchStoryResponse(
    val stories: List<StorySummaryDto> = emptyList()
)
