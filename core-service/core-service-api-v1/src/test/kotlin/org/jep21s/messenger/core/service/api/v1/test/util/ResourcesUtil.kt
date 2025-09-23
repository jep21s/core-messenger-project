package org.jep21s.messenger.core.service.api.v1.test.util

import com.fasterxml.jackson.module.kotlin.readValue
import org.jep21s.messenger.core.service.api.v1.ApiV1Mapper

inline fun <reified T> readResource(path: String): T =
  ApiV1Mapper.jacksonMapper.readValue<T>(
    Thread.currentThread()
      .contextClassLoader
      .getResource(path)
      ?.readBytes()
      ?: error("Resource [$path] not found")
  )
