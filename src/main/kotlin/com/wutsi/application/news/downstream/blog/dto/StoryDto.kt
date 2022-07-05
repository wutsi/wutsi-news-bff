package com.wutsi.application.news.downstream.blog.dto

import java.util.Date

data class StoryDto(
    val id: Long = -1,
    val siteId: Long = -1,
    val userId: Long = -1,
    val title: String? = null,
    val tagline: String? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null,
    val sourceUrl: String? = null,
    val sourceSite: String? = null,
    val wordCount: Int = 0,
    val readingMinutes: Int = 0,
    val language: String? = null,
    var content: String? = null,
    val contentType: String? = null,
    val status: StoryStatus = StoryStatus.draft,
    val creationDateTime: Date = Date(),
    val modificationDateTime: Date = Date(),
    val publishedDateTime: Date? = null,
    val tags: List<TagDto> = emptyList(),
    val slug: String = "",
    val readabilityScore: Int = 0,
    val topic: TopicDto? = null,
    val live: Boolean = false,
    val liveDateTime: Date? = null,
    val socialMediaMessage: String? = null,
    val scheduledPublishDateTime: Date? = null,
    val publishToSocialMedia: Boolean = false,
)
