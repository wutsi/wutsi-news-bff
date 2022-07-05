package com.wutsi.application.news.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.html.tag.TagProvider
import com.wutsi.editorjs.json.EJSJsonReader
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class EJSEditorConfig(
    private val mapper: ObjectMapper,
) {
    @Bean
    fun ejsJsonReader(): EJSJsonReader = EJSJsonReader(mapper)

    @Bean
    fun ejsHtmlWriter(): EJSHtmlWriter = EJSHtmlWriter(tagProvider())

    @Bean
    fun tagProvider(): TagProvider = TagProvider()
}
