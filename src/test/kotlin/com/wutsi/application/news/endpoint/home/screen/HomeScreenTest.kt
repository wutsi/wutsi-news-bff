package com.wutsi.application.news.endpoint.home.screen

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.news.downstream.blog.dto.SearchStoryResponse
import com.wutsi.application.news.downstream.blog.dto.SearchUserResponse
import com.wutsi.application.news.downstream.blog.dto.StorySummaryDto
import com.wutsi.application.news.downstream.blog.dto.UserSummaryDto
import com.wutsi.application.news.endpoint.AbstractEndpointTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.util.Date

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class HomeScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var blogApi: WutsiBlogApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val stories = listOf(
            createStorySummary(1, 11, "Story 1"),
            createStorySummary(2, 11, "Story 2"),
            createStorySummary(3, 13, "Story 3")
        )
        doReturn(SearchStoryResponse(stories)).whenever(blogApi).searchStories(any())

        val users = listOf(
            createUserSummary(11, "User 11"),
            createUserSummary(13, "User 13"),
        )
        doReturn(SearchUserResponse(users)).whenever(blogApi).searchUsers(any())
    }

    @Test
    fun index() {
        val url = "http://localhost:$port"
        assertEndpointEquals("/screens/home/index.json", url)
    }

    @Test
    fun author() {
        val url = "http://localhost:$port?author-id=11"
        assertEndpointEquals("/screens/home/author.json", url)
    }

    private fun createStorySummary(id: Long, userId: Long, title: String) = StorySummaryDto(
        id = id,
        title = title,
        thumbnailUrl = "https://www.img.png/$id.png",
        userId = userId,
        publishedDateTime = Date(),
    )

    private fun createUserSummary(id: Long, fullName: String) = UserSummaryDto(
        id = id,
        fullName = fullName,
        pictureUrl = "https://www.img.png/$id.png",
    )
}
