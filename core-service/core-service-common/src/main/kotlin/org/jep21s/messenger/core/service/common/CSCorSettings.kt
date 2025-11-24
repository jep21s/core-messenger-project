package org.jep21s.messenger.core.service.common

import org.jep21s.messenger.core.lib.logging.common.CMLoggerProvider
import org.jep21s.messenger.core.service.common.context.CSWorkMode
import org.jep21s.messenger.core.service.common.repo.IChatRepo
import org.jep21s.messenger.core.service.common.repo.IMessageRepo

object CSCorSettings {
  private val loggerProviderWrapper = InitWrapper<CMLoggerProvider>()
  val loggerProvider: CMLoggerProvider
    get() = loggerProviderWrapper.get(CSCorSettings::loggerProvider.name)

  private val chatRepoStubWrapper = InitWrapper<IChatRepo>()
  private val messageRepoStubWrapper = InitWrapper<IMessageRepo>()
  private val chatRepoTestWrapper = InitWrapper<IChatRepo>()
  private val messageRepoTestWrapper = InitWrapper<IMessageRepo>()
  private val chatRepoProdWrapper = InitWrapper<IChatRepo>()
  private val messageRepoProWrapper = InitWrapper<IMessageRepo>()


  fun initialize(
    loggerProvider: CMLoggerProvider? = null,
    chatRepoStub: IChatRepo? = null,
    messageRepoStub: IMessageRepo? = null,
    chatRepoTest: IChatRepo? = null,
    messageRepoTest: IMessageRepo? = null,
    chatRepoProd: IChatRepo? = null,
    messageRepoProd: IMessageRepo? = null,
  ) {
    loggerProvider?.let { loggerProviderWrapper.set(it, CSCorSettings::loggerProvider.name) }

    chatRepoStub?.let { chatRepoStubWrapper.set(it, CSCorSettings::chatRepoStubWrapper.name) }
    messageRepoStub?.let { messageRepoStubWrapper.set(it, CSCorSettings::messageRepoStubWrapper.name) }

    chatRepoTest?.let { chatRepoTestWrapper.set(it, CSCorSettings::chatRepoTestWrapper.name) }
    messageRepoTest?.let { messageRepoTestWrapper.set(it, CSCorSettings::messageRepoTestWrapper.name) }

    chatRepoProd?.let { chatRepoProdWrapper.set(it, CSCorSettings::chatRepoProdWrapper.name) }
    messageRepoProd?.let { messageRepoProWrapper.set(it, CSCorSettings::messageRepoProWrapper.name) }
  }

  fun chatRepo(
    workMode: CSWorkMode,
  ): IChatRepo = when (workMode) {
    is CSWorkMode.Stub -> chatRepoStubWrapper.get(CSCorSettings::chatRepoStubWrapper.name)
    is CSWorkMode.Test -> chatRepoTestWrapper.get(CSCorSettings::chatRepoTestWrapper.name)
    is CSWorkMode.Prod -> chatRepoProdWrapper.get(CSCorSettings::chatRepoProdWrapper.name)
  }

  fun messageRepo(
    workMode: CSWorkMode,
  ): IMessageRepo = when (workMode) {
    is CSWorkMode.Stub -> messageRepoStubWrapper.get(CSCorSettings::messageRepoStubWrapper.name)
    is CSWorkMode.Test -> messageRepoStubWrapper.get(CSCorSettings::messageRepoStubWrapper.name)
    is CSWorkMode.Prod -> messageRepoProWrapper.get(CSCorSettings::messageRepoProWrapper.name)
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