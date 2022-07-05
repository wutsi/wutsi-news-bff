package com.wutsi.application.news

import com.wutsi.application.shared.WutsiBffApplication
import com.wutsi.platform.core.WutsiApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@WutsiApplication
@WutsiBffApplication
@SpringBootApplication
class Application

fun main(vararg args: String) {
    org.springframework.boot.runApplication<Application>(*args)
}
