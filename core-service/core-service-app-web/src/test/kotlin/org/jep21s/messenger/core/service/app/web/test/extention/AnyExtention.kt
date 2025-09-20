package org.jep21s.messenger.core.service.app.web.test.extention

import com.fasterxml.jackson.module.kotlin.readValue
import org.jep21s.messenger.core.service.api.v1.ApiV1Mapper

fun<T> T.toLinkedHashMap(): Any = when (this) {
  is List<*> -> { map { it.toLinkedHashMap() } }
  else -> ApiV1Mapper.jacksonMapper
    .readValue<LinkedHashMap<String, Any?>>(
      ApiV1Mapper.jacksonMapper
        .writeValueAsString(this)
    )
}