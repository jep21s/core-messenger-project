package org.jep21s.messenger.core.service.repo.cassandra.extention

import java.util.concurrent.CompletionStage
import kotlinx.coroutines.future.await

suspend fun <T> List<CompletionStage<T>>.awaitAll(): List<T> = map { it.await() }