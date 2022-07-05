package com.wutsi.application.news.endpoint

import com.wutsi.application.shared.Theme
import com.wutsi.flutter.sdui.Action
import com.wutsi.flutter.sdui.Container
import com.wutsi.flutter.sdui.WidgetAware

abstract class AbstractQuery : AbstractEndpoint() {
    protected fun toSectionWidget(
        child: WidgetAware,
        padding: Double? = 10.0,
        background: String? = Theme.COLOR_WHITE,
        action: Action? = null
    ) = Container(
        padding = padding,
        margin = 5.0,
        border = 1.0,
        borderColor = Theme.COLOR_GRAY_LIGHT,
        background = background,
        width = Double.MAX_VALUE,
        child = child,
        action = action
    )
}
