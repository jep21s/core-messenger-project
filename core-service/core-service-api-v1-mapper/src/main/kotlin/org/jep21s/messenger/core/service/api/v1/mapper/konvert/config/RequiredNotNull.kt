package org.jep21s.messenger.core.service.api.v1.mapper.konvert.config

fun <T> T?.requireNotNull(fieldName: String): T {
  return requireNotNull(this) { "Field '$fieldName' is required" }
}