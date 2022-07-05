package com.wutsi.application.news.endpoint.read.screen

import com.fasterxml.jackson.databind.ObjectMapper
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.whenever
import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.news.downstream.blog.dto.GetStoryResponse
import com.wutsi.application.news.downstream.blog.dto.GetUserResponse
import com.wutsi.application.news.downstream.blog.dto.StoryDto
import com.wutsi.application.news.downstream.blog.dto.UserDto
import com.wutsi.application.news.endpoint.AbstractEndpointTest
import com.wutsi.editorjs.dom.Block
import com.wutsi.editorjs.dom.BlockData
import com.wutsi.editorjs.dom.BlockType
import com.wutsi.editorjs.dom.EJSDocument
import com.wutsi.editorjs.dom.File
import com.wutsi.editorjs.json.EJSJsonWriter
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.boot.test.web.server.LocalServerPort
import java.io.StringWriter

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
internal class ReadScreenTest : AbstractEndpointTest() {
    @LocalServerPort
    val port: Int = 0

    @MockBean
    private lateinit var blogApi: WutsiBlogApi

    @BeforeEach
    override fun setUp() {
        super.setUp()

        val story = createStory(1, 11, "Sample Story")
        doReturn(GetStoryResponse(story)).whenever(blogApi).getStory(any())

        val user = createUser(11, "Ray Sponsible")
        doReturn(GetUserResponse(user)).whenever(blogApi).getUser(any())
    }

    @Test
    fun index() {
        val url = "http://localhost:$port/read?id=1"
        assertEndpointEquals("/screens/read/index.json", url)
    }

    private fun createUser(id: Long, fullName: String) = UserDto(
        id = id,
        fullName = fullName,
        pictureUrl = "https://www.img.png/$id.png",
    )

    private fun createStory(id: Long, userId: Long, title: String) = StoryDto(
        id = id,
        title = title,
        userId = userId,
        content = toContent()
    )

    private fun toContent(): String {
        val doc = createDocument()
        val writer = StringWriter()
        EJSJsonWriter(ObjectMapper()).write(doc, writer)
        return writer.toString()
    }

    private fun createDocument() = EJSDocument(
        blocks = arrayListOf(
            Block(
                type = BlockType.header,
                data = BlockData(
                    level = 1,
                    text = "Editor.js"
                )
            ),
            Block(
                type = BlockType.paragraph,
                data = BlockData(
                    text = "Hey. Meet the new Editor. On this page you can see it in action — try to edit this text"
                )
            ),
            Block(
                type = BlockType.list,
                data = BlockData(
                    items = arrayListOf(
                        "It is a block-styled editor",
                        "It returns clean data output in JSON",
                        "Designed to be extendable and pluggable with a simple API"
                    )
                )
            ),
            Block(
                type = BlockType.delimiter
            ),
            Block(
                type = BlockType.image,
                data = BlockData(
                    caption = "Logo",
                    withBackground = true,
                    withBorder = true,
                    stretched = true,
                    file = File(
                        url = "/upload/temporary/o_488cfb382712d6af914301c73f376e8c.jpg"
                    )
                )
            ),
            Block(
                type = BlockType.code,
                data = BlockData(
                    code = "class Foo { }"
                )
            ),
        )
    )
}
