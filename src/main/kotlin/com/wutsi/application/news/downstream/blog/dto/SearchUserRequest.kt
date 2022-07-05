package com.wutsi.application.news.downstream.blog.dto

import javax.validation.constraints.NotNull

data class SearchUserRequest(
    @get:NotNull val siteId: Long? = 1L,
    val userIds: List<Long> = emptyList(),
    val limit: Int = 20,
    val offset: Int = 0,
    val sortBy: UserSortStrategy = UserSortStrategy.created,
    val sortOrder: SortOrder = SortOrder.ascending,
    val blog: Boolean? = null,
    val autoFollowedByBlogs: Boolean? = null,
    val testUser: Boolean? = false
)
