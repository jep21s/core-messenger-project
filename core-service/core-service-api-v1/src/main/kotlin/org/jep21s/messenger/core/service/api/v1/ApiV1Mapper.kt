package org.jep21s.messenger.core.service.api.v1

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

object ApiV1Mapper {
  val jacksonMapper = jacksonObjectMapper()
    .apply {
      disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
      enable(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES)
      enable(DeserializationFeature.FAIL_ON_NUMBERS_FOR_ENUMS)
      enable(DeserializationFeature.FAIL_ON_READING_DUP_TREE_KEY)
    }

  fun <T> serialize(value: T?): String = jacksonMapper.writeValueAsString(value)

  fun <T> deserialize(json: String, clazz: Class<T>): T =
    jacksonMapper.readValue(json, clazz)
}

inline fun <reified T> ApiV1Mapper.deserialize(json: String): T =
  deserialize(json, T::class.java)

