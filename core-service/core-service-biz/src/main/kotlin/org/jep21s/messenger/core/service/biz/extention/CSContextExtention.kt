package org.jep21s.messenger.core.service.biz.extention

import org.jep21s.messenger.core.service.common.context.CSContext

@Suppress("UNCHECKED_CAST")
fun <MReq, MResp> CSContext<MReq, MResp?>.putModelResp(
  modelResp: MResp,
): CSContext<MReq, MResp> =
  this.copy(modelResp = modelResp) as CSContext<MReq, MResp>