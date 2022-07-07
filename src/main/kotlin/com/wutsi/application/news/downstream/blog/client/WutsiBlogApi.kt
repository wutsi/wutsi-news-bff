package com.wutsi.application.news.downstream.blog.client

import com.wutsi.application.news.downstream.blog.dto.GetStoryResponse
import com.wutsi.application.news.downstream.blog.dto.GetUserResponse
import com.wutsi.application.news.downstream.blog.dto.RecommendStoryRequest
import com.wutsi.application.news.downstream.blog.dto.RecommendStoryResponse
import com.wutsi.application.news.downstream.blog.dto.SearchStoryRequest
import com.wutsi.application.news.downstream.blog.dto.SearchStoryResponse
import com.wutsi.application.news.downstream.blog.dto.SearchUserRequest
import com.wutsi.application.news.downstream.blog.dto.SearchUserResponse
import feign.Headers
import feign.Param
import feign.RequestLine

interface WutsiBlogApi {
    @RequestLine("POST /v1/story/search")
    @Headers(value = ["Content-Type: application/json"])
    fun searchStories(request: SearchStoryRequest): SearchStoryResponse

    @RequestLine("GET /v1/story/{id}")
    @Headers(value = ["Content-Type: application/json"])
    fun getStory(@Param("id") id: Long): GetStoryResponse

    @RequestLine("POST /v1/story/recommend")
    @Headers(value = ["Content-Type: application/json"])
    fun recommendStory(request: RecommendStoryRequest): RecommendStoryResponse

    @RequestLine("POST /v1/users/search")
    @Headers(value = ["Content-Type: application/json"])
    fun searchUsers(request: SearchUserRequest): SearchUserResponse

    @RequestLine("GET /v1/users/{id}")
    @Headers(value = ["Content-Type: application/json"])
    fun getUser(@Param("id") id: Long): GetUserResponse
}
