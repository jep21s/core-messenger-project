package org.jep21s.messenger.core.lib.konvert

fun <T> T?.requireNotNull(fieldName: String): T {
  return requireNotNull(this) { "Field '$fieldName' is required" }
}