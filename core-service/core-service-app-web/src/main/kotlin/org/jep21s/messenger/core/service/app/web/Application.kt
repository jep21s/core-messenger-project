package org.jep21s.messenger.core.service.app.web

import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.jep21s.messenger.core.service.app.web.module.initializeLoggerProvider
import org.jep21s.messenger.core.service.app.web.module.initializeRepos
import org.jep21s.messenger.core.service.app.web.module.restModule

fun main() {
  embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::csModule)
    .start(wait = true)
}

suspend fun Application.csModule() {
  initializeLoggerProvider()
  initializeRepos()
  restModule()
}
