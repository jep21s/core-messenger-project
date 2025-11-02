package org.jep21s.messenger.core.lib.test.common.extention

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue

private val jacksonMapper = jacksonObjectMapper()
  .apply {
    disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
    enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
    enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
  }

fun <T> T.toLinkedHashMap(): Any = when (this) {
  is List<*> -> {
    map { it.toLinkedHashMap() }
  }

  else -> jacksonMapper
    .readValue<LinkedHashMap<String, Any?>>(
      jacksonMapper
        .writeValueAsString(this)
    )
}