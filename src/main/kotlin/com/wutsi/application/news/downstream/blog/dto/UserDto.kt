package com.wutsi.application.news.downstream.blog.dto

import java.util.Date

data class UserDto(
    val id: Long = -1,
    val siteId: Long = -1,
    val name: String = "",
    val fullName: String = "",
    val biography: String? = null,
    val email: String? = null,
    val pictureUrl: String? = null,
    val loginCount: Long = 0,
    val language: String? = null,
    val lastLoginDateTime: Date? = null,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val websiteUrl: String? = null,
    val superUser: Boolean = false,
    val readAllLanguages: Boolean? = null,
    val facebookId: String? = null,
    val twitterId: String? = null,
    val linkedinId: String? = null,
    val youtubeId: String? = null,
    val whatsappId: String? = null,
    var telegramId: String? = null,
    val blog: Boolean = false,
    val storyCount: Long = 0L,
    val followerCount: Long = 0L,
    val subscriberCount: Long = 0L,
    val lastPublicationDateTime: Date? = null,
    val testUser: Boolean = false,
)
