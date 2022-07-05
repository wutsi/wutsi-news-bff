package com.wutsi.application.news.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.shared.service.FeignAcceptLanguageInterceptor
import com.wutsi.platform.core.tracing.feign.FeignTracingRequestInterceptor
import com.wutsi.platform.core.util.feign.Custom5XXErrorDecoder
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment

@Configuration
class WutsiBlogApiConfiguration(
    private val tracingRequestInterceptor: FeignTracingRequestInterceptor,
    private val acceptLanguageInterceptor: FeignAcceptLanguageInterceptor,
    private val mapper: ObjectMapper,
    private val env: Environment
) {
    @Bean
    fun blogApi(): WutsiBlogApi =
        WutsiBlogApiBuilder().build(
            env = environment(),
            mapper = mapper,
            interceptors = listOf(
                tracingRequestInterceptor,
                acceptLanguageInterceptor
            ),
            errorDecoder = Custom5XXErrorDecoder()
        )

    private fun environment(): WutsiBlogEnvironment =
        WutsiBlogEnvironment.PRODUCTION
}
