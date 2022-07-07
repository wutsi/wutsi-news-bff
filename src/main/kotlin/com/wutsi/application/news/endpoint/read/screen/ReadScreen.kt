package com.wutsi.application.news.endpoint.read.screen

import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.news.downstream.blog.client.WutsiBlogTrackingApi
import com.wutsi.application.news.downstream.blog.dto.PushTrackRequest
import com.wutsi.application.news.downstream.blog.dto.RecommendStoryRequest
import com.wutsi.application.news.downstream.blog.dto.SearchStoryContext
import com.wutsi.application.news.downstream.blog.dto.StoryDto
import com.wutsi.application.news.endpoint.AbstractQuery
import com.wutsi.application.news.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.model.AccountModel
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.editorjs.html.EJSHtmlWriter
import com.wutsi.editorjs.json.EJSJsonReader
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Flexible
import com.wutsi.flutter.sdui.Html
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.Alignment
import com.wutsi.flutter.sdui.enums.BoxFit
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.TextAlignment
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.tracing.TracingContext
import com.wutsi.platform.tenant.dto.Tenant
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.UUID

@RestController
@RequestMapping("/read")
class ReadScreen(
    private val blogApi: WutsiBlogApi,
    private val blogTrackingApi: WutsiBlogTrackingApi,
    private val ejsHtmlWriter: EJSHtmlWriter,
    private val ejsJsonReader: EJSJsonReader,
    private val tracingContext: TracingContext,
    private val tenantProvider: TenantProvider,
    private val imageService: ImageService,
) : AbstractQuery() {
    companion object {
        const val RECOMMEND_IMAGE_WIDTH = 75.0
        const val RECOMMEND_IMAGE_HEIGHT = 75.0

        private val LOGGER = LoggerFactory.getLogger(ReadScreen::class.java)
    }

    @PostMapping
    fun index(@RequestParam id: Long): Widget {
        val story = blogApi.getStory(id).story
        val author = blogApi.getUser(story.userId).user
        val tenant = tenantProvider.get()

        try {
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
                        children = listOfNotNull(
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
                            if (story.tagline.isNullOrEmpty())
                                null
                            else
                                Container(
                                    padding = 10.0,
                                    alignment = Alignment.Center,
                                    child = Text(
                                        caption = story.tagline,
                                        color = Theme.COLOR_GRAY,
                                        italic = true,
                                        alignment = TextAlignment.Center
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
                                    Container(padding = 5.0),
                                    author.let {
                                        Container(
                                            child = Text(
                                                caption = it.fullName.uppercase(),
                                                size = Theme.TEXT_SIZE_SMALL,
                                                overflow = TextOverflow.Elipsis,
                                                color = Theme.COLOR_PRIMARY,
                                                decoration = TextDecoration.Underline
                                            ),
                                            action = gotoUrl(urlBuilder.build("/?user-id=${story.userId}"))
                                        )
                                    }
                                )
                            ),
                            Divider(color = Theme.COLOR_DIVIDER),
                            Html(
                                data = story.content?.let { toHtml(it) }
                            ),
                            toRecommendWidget(id, tenant)
                        )
                    )
                )
            ).toWidget()
        } finally {
            // Send tracking event to wutsi-blog so that it knows that this device read the story
            val now = System.currentTimeMillis()
            trackEvent(id, now, "readstart")
            trackEvent(id, now + story.readingMinutes * 60 * 1000, "readend")
        }
    }

    private fun trackEvent(id: Long, time: Long, event: String) {
        try {
            blogTrackingApi.push(
                request = PushTrackRequest(
                    time = time,
                    duid = tracingContext.deviceId(),
                    pid = id.toString(),
                    hid = UUID.randomUUID().toString(),
                    page = "page.read",
                    event = event
                )
            )
        } catch (ex: Exception) {
            LOGGER.warn("Unable to push tracking event", ex)
        }
    }

    private fun toHtml(json: String): String {
        val writer = StringWriter()
        val doc = ejsJsonReader.read(json)
        ejsHtmlWriter.write(doc, writer)
        return writer.toString()
    }

    private fun toRecommendWidget(id: Long, tenant: Tenant): WidgetAware? {
        val stories = blogApi.recommendStory(
            request = RecommendStoryRequest(
                storyId = id,
                context = SearchStoryContext(
                    deviceId = tracingContext.deviceId()
                )
            )
        ).stories
            .filter { !it.thumbnailUrl.isNullOrEmpty() && !it.title.isNullOrEmpty() }
            .take(3)

        if (stories.isEmpty())
            return null

        val children = mutableListOf<WidgetAware>()
        children.addAll(
            listOf(
                Divider(color = Theme.COLOR_DIVIDER, height = 1.0),
                Container(
                    padding = 10.0,
                    child = Text(
                        caption = getText("page.home.read-also"),
                        size = Theme.TEXT_SIZE_LARGE
                    )
                )
            )
        )

        val fmt = SimpleDateFormat(tenant.dateFormat)
        children.addAll(
            stories.flatMap {
                listOf(
                    Container(
                        padding = 10.0,
                        action = gotoUrl(urlBuilder.build("/read?id=${it.id}")),
                        child = Row(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.start,
                            children = listOf(
                                Flexible(
                                    flex = 3,
                                    child = Image(
                                        url = imageService.transform(
                                            url = it.thumbnailUrl!!,
                                            transformation = Transformation(
                                                dimension = Dimension(
                                                    width = RECOMMEND_IMAGE_WIDTH.toInt(),
                                                    height = RECOMMEND_IMAGE_HEIGHT.toInt()
                                                )
                                            )
                                        ),
                                        fit = BoxFit.cover
                                    ),
                                ),
                                Flexible(
                                    flex = 1,
                                    child = Container()
                                ),
                                Flexible(
                                    flex = 8,
                                    child = Column(
                                        mainAxisAlignment = MainAxisAlignment.start,
                                        crossAxisAlignment = CrossAxisAlignment.start,
                                        children = listOf(
                                            Text(it.title!!, bold = true, maxLines = 3),
                                            Container(padding = 10.0),
                                            Text(
                                                fmt.format(it.publishedDateTime),
                                                size = Theme.TEXT_SIZE_SMALL,
                                                color = Theme.COLOR_GRAY
                                            )
                                        )
                                    )
                                )
                            )
                        )
                    ),
                )
            }
        )
        return Column(
            mainAxisAlignment = MainAxisAlignment.start,
            crossAxisAlignment = CrossAxisAlignment.start,
            children = children
        )
    }

    private fun toStoryWidget(story: StoryDto, tenant: Tenant): WidgetAware =
        TODO()
}
