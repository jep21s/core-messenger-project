package org.jep21s.messenger.core.service.app.web.test.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.application.Application
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.lib.logging.logback.cmLoggerLogback
import org.jep21s.messenger.core.service.app.web.module.initInmemoryRepos
import org.jep21s.messenger.core.service.app.web.module.restModule
import org.jep21s.messenger.core.service.common.CSCorSettings

fun testConfiguredApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
  testApplication {
    application {
      testCsCorSettingsModule()
      restModule()
      initInmemoryRepos()
    }

    val client = createClient {
      install(ContentNegotiation) {
        jackson {
          disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)

          enable(SerializationFeature.INDENT_OUTPUT)
          writerWithDefaultPrettyPrinter()
        }
      }
    }

    block(client)
  }
}

private fun Application.testCsCorSettingsModule() = runCatching {
  CSCorSettings.initialize(
    loggerProvider = CMLoggerProvider { clazz -> cmLoggerLogback(clazz) }
  )
}