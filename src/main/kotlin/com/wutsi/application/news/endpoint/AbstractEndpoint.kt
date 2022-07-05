package com.wutsi.application.news.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.wutsi.application.shared.service.SecurityContext
import com.wutsi.application.shared.service.SharedUIMapper
import com.wutsi.application.shared.service.TogglesProvider
import com.wutsi.application.shared.service.URLBuilder
import com.wutsi.application.shared.ui.BottomNavigationBarWidget
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.enums.ActionType
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder

abstract class AbstractEndpoint {
    @Autowired
    protected lateinit var messages: MessageSource

    @Autowired
    protected lateinit var securityContext: SecurityContext

    @Autowired
    protected lateinit var urlBuilder: URLBuilder

    @Autowired
    protected lateinit var togglesProvider: TogglesProvider

    @Autowired
    protected lateinit var sharedUIMapper: SharedUIMapper

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Value("\${wutsi.application.shell-url}")
    private lateinit var shellUrl: String

    @Value("\${wutsi.application.cash-url}")
    private lateinit var cashUrl: String

    protected fun gotoUrl(
        url: String,
        replacement: Boolean? = null,
        parameters: Map<String, String>? = null
    ) = Action(
        type = ActionType.Route,
        url = url,
        replacement = replacement,
        parameters = parameters
    )

    protected fun getText(key: String, args: Array<Any?> = emptyArray()) =
        messages.getMessage(key, args, LocaleContextHolder.getLocale()) ?: key

    protected fun bottomNavigationBar() = BottomNavigationBarWidget(
        model = sharedUIMapper.toBottomNavigationBarModel(
            shellUrl = shellUrl,
            cashUrl = cashUrl,
            togglesProvider = togglesProvider,
            urlBuilder = urlBuilder
        )
    ).toBottomNavigationBar()
}
