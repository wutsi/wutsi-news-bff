package com.wutsi.application.news.downstream.blog.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.ecommerce.order.WutsiOrderApi
import feign.RequestInterceptor
import feign.codec.ErrorDecoder

class WutsiBlogTrackingApiBuilder {
    fun build(
        env: WutsiBlogTrackingEnvironment,
        mapper: ObjectMapper,
        interceptors: List<RequestInterceptor> = emptyList(),
        errorDecoder: ErrorDecoder = ErrorDecoder.Default(),
        retryPeriodMillis: Long = 100L,
        retryMaxPeriodSeconds: Long = 3,
        retryMaxAttempts: Int = 5,
        connectTimeoutMillis: Long = 15000,
        readTimeoutMillis: Long = 15000,
        followRedirects: Boolean = true,
    ) = feign.Feign.builder()
        .client(feign.okhttp.OkHttpClient())
        .encoder(feign.jackson.JacksonEncoder(mapper))
        .decoder(feign.jackson.JacksonDecoder(mapper))
        .logger(feign.slf4j.Slf4jLogger(WutsiOrderApi::class.java))
        .logLevel(feign.Logger.Level.BASIC)
        .requestInterceptors(interceptors)
        .errorDecoder(errorDecoder)
        .retryer(
            feign.Retryer.Default(
                retryPeriodMillis,
                java.util.concurrent.TimeUnit.SECONDS.toMillis(retryMaxPeriodSeconds),
                retryMaxAttempts
            )
        )
        .options(
            feign.Request.Options(
                connectTimeoutMillis,
                java.util.concurrent.TimeUnit.MILLISECONDS,
                readTimeoutMillis,
                java.util.concurrent.TimeUnit.MILLISECONDS,
                followRedirects
            )
        )
        .target(WutsiBlogTrackingApi::class.java, env.url)
}
