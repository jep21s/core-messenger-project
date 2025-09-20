package org.jep21s.messenger.core.service.app.web.test.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.jackson.jackson
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import org.jep21s.messenger.core.service.app.web.csModule

fun testConfiguredApplication(block: suspend ApplicationTestBuilder.(HttpClient) -> Unit) {
  testApplication {
    application { csModule() }

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