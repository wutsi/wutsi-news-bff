package com.wutsi.application.news.downstream.blog.dto

data class SearchUserResponse(
    val users: List<UserSummaryDto> = emptyList()
)
