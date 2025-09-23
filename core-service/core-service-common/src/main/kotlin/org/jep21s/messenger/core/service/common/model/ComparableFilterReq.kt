package org.jep21s.messenger.core.service.common.model

import java.time.Instant

data class ComparableFilter (
  val value: Instant,
  val direction: ConditionType,
)