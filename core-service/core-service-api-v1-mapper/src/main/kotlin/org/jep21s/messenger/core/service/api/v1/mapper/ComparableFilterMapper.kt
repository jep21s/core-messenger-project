package org.jep21s.messenger.core.service.api.v1.mapper

import io.mcarle.konvert.api.Konfig
import io.mcarle.konvert.api.Konverter
import org.jep21s.messenger.core.service.api.v1.models.ComparableFilterReq
import org.jep21s.messenger.core.service.common.model.ComparableFilter

@Konverter(
  options = [
    Konfig(key = "konvert.enforce-not-null", value = "true"),
    Konfig(key = "konvert.invalid-mapping-strategy", value = "fail")
  ]
)
interface ComparableFilterMapper {
  fun mapToModel(request: ComparableFilterReq): ComparableFilter
}