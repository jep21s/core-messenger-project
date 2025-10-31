package org.jep21s.messenger.core.service.common

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSCorSettings {
  private val loggerProviderWrapper = InitWrapper<CMLoggerProvider>()
  val loggerProvider: CMLoggerProvider
    get() = loggerProviderWrapper.get(CSCorSettings::loggerProvider.name)

  private val chatRepoStubWrapper = InitWrapper<IChatRepo>()
  val chatRepoStub: IChatRepo
    get() = chatRepoStubWrapper.get(CSCorSettings::chatRepoStub.name)

  private val messageRepoStubWrapper = InitWrapper<IMessageRepo>()
  val messageRepoStub: IMessageRepo
    get() = messageRepoStubWrapper.get(CSCorSettings::messageRepoStub.name)


  fun initialize(
    loggerProvider: CMLoggerProvider? = null,
    chatRepoStub: IChatRepo? = null,
    messageRepoStub: IMessageRepo? = null,
  ) {
    loggerProvider?.let { loggerProviderWrapper.set(it, CSCorSettings::loggerProvider.name) }
    chatRepoStub?.let { chatRepoStubWrapper.set(it, CSCorSettings::chatRepoStub.name) }
    messageRepoStub?.let { messageRepoStubWrapper.set(it, CSCorSettings::messageRepoStub.name) }
  }
}

private class InitWrapper<T>() {
  private var value: T? = null

  fun get(fieldName: String): T = value.let {
    if (it == null) notInit(fieldName)
    return@let it
  }

  fun set(initValue: T, fieldName: String) {
    if (value != null) alreadyInit(fieldName)
    value = initValue
    successInit(fieldName)
  }
}

private fun notInit(fieldName: String): Nothing =
  error("$fieldName not initialized")

private fun alreadyInit(fieldName: String): Nothing =
  error("$fieldName already initialized")

private fun successInit(fieldName: String) =
  println("----- $fieldName success initialized -----")