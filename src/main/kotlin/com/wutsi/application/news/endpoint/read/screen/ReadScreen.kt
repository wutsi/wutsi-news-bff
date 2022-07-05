package com.wutsi.application.news.endpoint.read.screen

import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.news.endpoint.AbstractQuery
import com.wutsi.application.news.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.model.AccountModel
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Html
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.flutter.sdui.enums.TextOverflow
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter

@RestController
@RequestMapping("/read")
class ReadScreen(
    private val blogApi: WutsiBlogApi,
    private val ejsHtmlWriter: EJSHtmlWriter,
    private val ejsJsonReader: EJSJsonReader,
) : AbstractQuery() {

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val story = blogApi.getStory(id).story
        val author = blogApi.getUser(story.userId).user

        return Screen(
            id = Page.READ,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = author.fullName.uppercase(),
            ),
            bottomNavigationBar = bottomNavigationBar(),
            child = SingleChildScrollView(
                child = Column(
                    mainAxisAlignment = MainAxisAlignment.start,
                    crossAxisAlignment = CrossAxisAlignment.start,
                    children = listOf(
                        Container(
                            padding = 10.0,
                            child = Text(
                                caption = story.title ?: "",
                                size = Theme.TEXT_SIZE_LARGE,
                                bold = true,
                                maxLines = 3,
                                overflow = TextOverflow.Elipsis
                            ),
                        ),
                        Row(
                            children = listOfNotNull(
                                Container(padding = 10.0),
                                author.let {
                                    Avatar(
                                        model = AccountModel(
                                            id = it.id,
                                            displayName = it.fullName,
                                            pictureUrl = it.pictureUrl
                                        ),
                                        radius = 12.0,
                                        action = gotoUrl(urlBuilder.build("/?author-id=${story.userId}"))
                                    )
                                },
                                Container(padding = 10.0),
                                author.let {
                                    Container(
                                        child = Text(
                                            caption = it.fullName.uppercase(),
                                            size = Theme.TEXT_SIZE_SMALL,
                                            overflow = TextOverflow.Elipsis,
                                            color = Theme.COLOR_PRIMARY,
                                            decoration = TextDecoration.Underline
                                        ),
                                        action = gotoUrl(urlBuilder.build("/?author-id=${story.userId}"))
                                    )
                                }
                            )
                        ),
                        Divider(color = Theme.COLOR_DIVIDER),
                        Html(
                            data = story.content?.let { toHtml(it) }
                        ),
                    )
                )
            )
        ).toWidget()
    }

    private fun toHtml(json: String): String {
        val writer = StringWriter()
        val doc = ejsJsonReader.read(json)
        ejsHtmlWriter.write(doc, writer)
        return writer.toString()
    }
}
