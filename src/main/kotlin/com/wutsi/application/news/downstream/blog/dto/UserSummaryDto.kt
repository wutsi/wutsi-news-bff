package com.wutsi.application.news.downstream.blog.dto

import java.util.Date

data class UserSummaryDto(
    val id: Long = -1,
    val siteId: Long = -1,
    val name: String = "",
    val blog: Boolean = false,
    val fullName: String = "",
    val pictureUrl: String? = null,
    val biography: String? = null,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val storyCount: Long = 0L,
    val followerCount: Long = 0L,
    val subscriberCount: Long = 0L,
    val testUser: Boolean = false,
    val email: String? = null
)
