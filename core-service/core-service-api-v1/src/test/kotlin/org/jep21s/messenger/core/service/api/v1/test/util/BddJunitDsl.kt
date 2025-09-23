package org.jep21s.messenger.core.service.api.v1.test.util

import org.junit.jupiter.api.DynamicContainer
import org.junit.jupiter.api.DynamicNode
import org.junit.jupiter.api.DynamicTest

class BddDynamicTestContainer {
  private val _givenList = mutableListOf<DynamicContainer>()

  val givenList: List<DynamicContainer>
    get() = _givenList

  fun add(dynamicContainer: DynamicContainer) = _givenList.add(dynamicContainer)
}

class GivenTestContainer(
  val blockName: String,
) {
  private val _whenList = mutableListOf<DynamicNode>()

  val whenList: List<DynamicNode>
    get() = _whenList

  fun add(dynamicNode: DynamicNode) = _whenList.add(dynamicNode)
}

class WhenTestContainer(
  val blockName: String,
  val givenParent: GivenTestContainer,
) {
  private val _thenList = mutableListOf<DynamicTest>()

  val thenList: List<DynamicTest>
    get() = _thenList

  fun add(dynamicTest: DynamicTest) = _thenList.add(dynamicTest)
}

fun runDynamicTest(
  block: BddDynamicTestContainer.() -> Unit,
): Collection<DynamicContainer> {
  val bddDynamicTestContainer = BddDynamicTestContainer().apply(block)
  return bddDynamicTestContainer.givenList
}

fun BddDynamicTestContainer.Given(
  displayName: String,
  block: GivenTestContainer.() -> Unit,
) {
  val givenBlockName: String = displayName.addGivenPrefix()
  val givenTestContainer = GivenTestContainer(givenBlockName).apply {
    runCatching { block() }
      .onFailure { fallbackGivenCondition(it) }
  }

  add(
    DynamicContainer.dynamicContainer(
      givenBlockName,
      givenTestContainer.whenList
    )
  )
}

private fun GivenTestContainer.fallbackGivenCondition(ex: Throwable) {
  if (isAnyWhenConditionWasStarted()) throw ex

  val fallbackDisplayName = blockName
  add(DynamicTest.dynamicTest(fallbackDisplayName) { throw ex })
}

private fun GivenTestContainer.isAnyWhenConditionWasStarted(): Boolean =
  whenList.isNotEmpty()

fun GivenTestContainer.When(
  displayName: String,
  block: WhenTestContainer.() -> Unit,
) {
  val whenBlockName: String = displayName.addWhenPrefix()
  val whenTestContainer = WhenTestContainer(whenBlockName, this)
    .apply {
      runCatching { block() }
        .onFailure { fallbackWhenCondition(it) }
    }

  add(
    DynamicContainer.dynamicContainer(
      displayName.addWhenPrefix(),
      whenTestContainer.thenList
    )
  )
}

private fun WhenTestContainer.fallbackWhenCondition(ex: Throwable) {
  if (isAnyThenConditionWasStarted()) throw ex

  val fallbackDisplayName = "${givenParent.blockName} | $blockName"
  add(DynamicTest.dynamicTest(fallbackDisplayName) { throw ex })
}

private fun WhenTestContainer.isAnyThenConditionWasStarted(): Boolean =
  thenList.isNotEmpty()

fun WhenTestContainer.Then(
  displayName: String,
  block: () -> Unit,
) {
  val thenBlockName: String =
    "${givenParent.blockName} | $blockName | ${displayName.addThenPrefix()}"
  add(
    DynamicTest.dynamicTest(
      thenBlockName,
      block
    )
  )
}

private fun String.addGivenPrefix(): String {
  if (isBlank()) return this
  return "Given: $this"
}

private fun String.addWhenPrefix(): String {
  if (isBlank()) return this
  return "When: $this"
}

private fun String.addThenPrefix(): String {
  if (isBlank()) return this
  return "Then: $this"
}
