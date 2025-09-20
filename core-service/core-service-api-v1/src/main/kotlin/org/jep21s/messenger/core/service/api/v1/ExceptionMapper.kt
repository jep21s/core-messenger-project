package org.jep21s.messenger.core.service.api.v1

import org.jep21s.messenger.core.service.api.v1.models.CSErrorResp

fun Throwable.asCSErrorResp(
  code: String = "unknown",
  group: String = "exceptions",
  message: String = this.message ?: "",
) = CSErrorResp(
  code = code,
  group = group,
  field = "",
  message = message,
)