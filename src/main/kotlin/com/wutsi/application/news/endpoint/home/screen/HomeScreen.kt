package com.wutsi.application.news.endpoint.home.screen

import com.wutsi.application.news.downstream.blog.client.WutsiBlogApi
import com.wutsi.application.news.downstream.blog.dto.SearchStoryContext
import com.wutsi.application.news.downstream.blog.dto.SearchStoryRequest
import com.wutsi.application.news.downstream.blog.dto.SearchUserRequest
import com.wutsi.application.news.downstream.blog.dto.StorySortStrategy
import com.wutsi.application.news.downstream.blog.dto.StoryStatus
import com.wutsi.application.news.downstream.blog.dto.StorySummaryDto
import com.wutsi.application.news.downstream.blog.dto.UserSummaryDto
import com.wutsi.application.news.endpoint.AbstractQuery
import com.wutsi.application.news.endpoint.Page
import com.wutsi.application.shared.Theme
import com.wutsi.application.shared.model.AccountModel
import com.wutsi.application.shared.service.TenantProvider
import com.wutsi.application.shared.ui.Avatar
import com.wutsi.flutter.sdui.AppBar
import com.wutsi.flutter.sdui.Column
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.Divider
import com.wutsi.flutter.sdui.Image
import com.wutsi.flutter.sdui.Row
import com.wutsi.flutter.sdui.Screen
import com.wutsi.flutter.sdui.SingleChildScrollView
import com.wutsi.flutter.sdui.Text
import com.wutsi.flutter.sdui.Widget
import com.wutsi.flutter.sdui.WidgetAware
import com.wutsi.flutter.sdui.enums.CrossAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisAlignment
import com.wutsi.flutter.sdui.enums.MainAxisSize
import com.wutsi.flutter.sdui.enums.TextDecoration
import com.wutsi.flutter.sdui.enums.TextOverflow
import com.wutsi.platform.core.image.AspectRatio
import com.wutsi.platform.core.image.Dimension
import com.wutsi.platform.core.image.Focus
import com.wutsi.platform.core.image.ImageService
import com.wutsi.platform.core.image.Transformation
import com.wutsi.platform.core.logging.KVLogger
import com.wutsi.platform.core.tracing.TracingContext
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.text.SimpleDateFormat

@RestController
@RequestMapping("/")
class HomeScreen(
    private val blogApi: WutsiBlogApi,
    private val tenantProvider: TenantProvider,
    private val tracingContext: TracingContext,
    private val imageService: ImageService,
    private val logger: KVLogger
) : AbstractQuery() {
    @PostMapping
    fun index(@RequestParam("user-id") userId: Long? = null): Widget {
        val stories = findStories(userId, userId != null)
        logger.add("story_count", stories.size)

        val users = findUsers(stories).associateBy { it.id }
        logger.add("user_count", users.size)

        val tenant = tenantProvider.get()
        val fmt = SimpleDateFormat(tenant.dateFormat, LocaleContextHolder.getLocale())
        return Screen(
            id = Page.HOME,
            backgroundColor = Theme.COLOR_GRAY_LIGHT,
            appBar = AppBar(
                elevation = 0.0,
                backgroundColor = Theme.COLOR_WHITE,
                foregroundColor = Theme.COLOR_BLACK,
                title = userId?.let { users[it]?.fullName?.uppercase() } ?: getText("page.home.app-bar.title"),
            ),
            bottomNavigationBar = bottomNavigationBar(),
            child = SingleChildScrollView(
                child = Column(
                    children = stories.map {
                        toStoryCardWidget(it, users[it.userId], fmt)
                    }
                )
            ),
        ).toWidget()
    }

    private fun findStories(userId: Long?, includeStoriesWithNoThumbnail: Boolean): List<StorySummaryDto> =
        blogApi.searchStories(
            request = SearchStoryRequest(
                siteId = tenantProvider.tenantId(),
                userIds = userId?.let { listOf(it) } ?: emptyList(),
                status = StoryStatus.published,
                context = SearchStoryContext(
                    deviceId = tracingContext.deviceId()
                ),
                limit = 100,
                sortBy = StorySortStrategy.recommended,
                dedupUser = (userId == null)
            )
        ).stories.filter { includeStoriesWithNoThumbnail || !it.thumbnailUrl.isNullOrEmpty() }

    private fun findUsers(stories: List<StorySummaryDto>): List<UserSummaryDto> {
        val userIds = stories.map { it.userId }.toSet()
        return if (userIds.isEmpty())
            emptyList()
        else
            blogApi.searchUsers(
                request = SearchUserRequest(
                    userIds = userIds.toList(),
                    limit = userIds.size
                )
            ).users
    }

    private fun toStoryCardWidget(story: StorySummaryDto, author: UserSummaryDto?, fmt: SimpleDateFormat): WidgetAware =
        toSectionWidget(
            padding = null,
            child = Column(
                mainAxisSize = MainAxisSize.min,
                mainAxisAlignment = MainAxisAlignment.start,
                crossAxisAlignment = CrossAxisAlignment.start,
                children = listOfNotNull(
                    story.thumbnailUrl?.let {
                        Image(
                            url = imageService.transform(
                                url = it,
                                transformation = Transformation(
                                    aspectRatio = AspectRatio(600, 400),
                                    focus = Focus.AUTO,
                                    dimension = Dimension(600, 400)
                                )
                            ),
                        )
                    },
                    story.thumbnailUrl?.let {
                        Divider(height = 1.0, color = Theme.COLOR_DIVIDER)
                    },
                    Container(
                        padding = 10.0,
                        child = Row(
                            children = listOfNotNull(
                                author?.let {
                                    Avatar(
                                        model = AccountModel(
                                            id = it.id,
                                            displayName = it.fullName,
                                            pictureUrl = it.pictureUrl
                                        ),
                                        radius = 12.0
                                    )
                                },
                                Container(padding = 5.0),
                                author?.let {
                                    Text(
                                        caption = it.fullName.uppercase(),
                                        size = Theme.TEXT_SIZE_SMALL,
                                        color = Theme.COLOR_PRIMARY,
                                        decoration = TextDecoration.Underline
                                    )
                                }
                            )
                        ),
                        action = gotoUrl(
                            urlBuilder.build("?user-id=${story.userId}")
                        )
                    ),
                    Container(
                        padding = 10.0,
                        child = Column(
                            mainAxisAlignment = MainAxisAlignment.start,
                            crossAxisAlignment = CrossAxisAlignment.start,
                            children = listOf(
                                Text(
                                    caption = story.title ?: "",
                                    size = Theme.TEXT_SIZE_LARGE,
                                    bold = true,
                                    maxLines = 5,
                                    overflow = TextOverflow.Elipsis
                                ),
                                Container(padding = 5.0),
                                Text(
                                    caption = fmt.format(story.publishedDateTime),
                                    size = Theme.TEXT_SIZE_SMALL,
                                    color = Theme.COLOR_GRAY
                                ),
                            )
                        )
                    )
                )
            ),
            action = gotoUrl(urlBuilder.build("/read?id=${story.id}"))
        )
}
