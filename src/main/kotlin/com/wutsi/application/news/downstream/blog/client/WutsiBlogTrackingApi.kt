package com.wutsi.application.news.downstream.blog.client

import com.wutsi.application.news.downstream.blog.dto.PushTrackRequest
import com.wutsi.application.news.downstream.blog.dto.PushTrackResponse
import feign.Headers
import feign.RequestLine

interface WutsiBlogTrackingApi {
    @RequestLine("POST /v1/tracks")
    @Headers("Content-Type: application/json")
    fun push(request: PushTrackRequest): PushTrackResponse
}
