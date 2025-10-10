package org.jep21s.messenger.core.service.api.v1.mapper.helper

import arrow.core.Either
import arrow.core.getOrElse
import arrow.core.left
import arrow.core.right
import kotlin.collections.forEach
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.jvmErasure

fun <A, B> Either<A, B>.getOrThrow(): B {
  return getOrElse { throw IllegalArgumentException() }
}

class MapEitherDslConfig<ModelReq>(
  val modelReq: () -> ModelReq,
  val innerFieldsEither: () -> List<Either<MappingNullError, Any>?>,
)

class MapEitherDsl<ModelReq> {
  private lateinit var _modelReq: () -> ModelReq
  private var _innerFieldsEither: () -> List<Either<MappingNullError, Any>?> = { emptyList() }

  fun modelReq(block: () -> ModelReq) {
    _modelReq = block
  }

  fun checkInnerFields(block: () -> List<Either<MappingNullError, Any>?>) {
    _innerFieldsEither = block
  }

  fun build() = MapEitherDslConfig(_modelReq, _innerFieldsEither)
}

inline fun <T : Any?, reified R : Any> buildEitherResult(
  request: T?,
  fieldName: String,
  vararg kProperties: KProperty1<T, *> = emptyArray(),
  noinline block: MapEitherDsl<R>.(T) -> Unit,
): Either<MappingNullError, R> = buildEitherResult(
  request = request,
  fieldName = fieldName,
  kClass = R::class,
  kProperties = kProperties,
  block = block
)

fun <T : Any?, R : Any> buildEitherResult(
  request: T?,
  fieldName: String,
  kClass: KClass<R>,
  vararg kProperties: KProperty1<T, *> = emptyArray(),
  block: MapEitherDsl<R>.(T) -> Unit,
): Either<MappingNullError, R> = when {
  request == null -> handleNullRequest(fieldName, kClass)
  else -> handleNotNullRequest(request, fieldName, kProperties, block)
}

private fun <R : Any> handleNullRequest(
  fieldName: String,
  kClass: KClass<R>,
): Either<MappingNullError, R> {
  val errors: List<MappingNullError> = kClass.memberProperties
    .mapNotNull { checkAllInnerFields(it) }
  return MappingNullError(fieldName, errors).left()
}

private fun <T : Any?, R : Any> handleNotNullRequest(
  request: T,
  fieldName: String,
  kProperties: Array<out KProperty1<T, *>>,
  block: MapEitherDsl<R>.(T) -> Unit,
): Either<MappingNullError, R> {
  val mappingNullError: MappingNullError? =
    buildMappingNullErrors(requireNotNull(request), kProperties)
      .takeIf { it.isNotEmpty() }
      ?.let { MappingNullError(fieldName, it) }
  val config: MapEitherDslConfig<R> = MapEitherDsl<R>()
    .apply { block(request) }
    .build()

  val mappingNullErrorResult: MappingNullError? = buildMappingNullErrorIfExists(
    mappingNullError,
    fieldName,
    config.innerFieldsEither()
  )
  return mappingNullErrorResult?.left()
    ?: config.modelReq().right()
}

private fun <T : Any?> buildMappingNullErrors(
  request: T & Any,
  kProperties: Array<out KProperty1<T, *>>,
): List<MappingNullError> = buildList {
  kProperties.forEach { kProperty ->
    if (kProperty.isMarkedNullable) {
      val fieldValue: Any? = kProperty.getter.call(request)
      if (fieldValue == null) add(MappingNullError(kProperty.name))
    }
  }
}

private fun buildMappingNullErrorIfExists(
  error: MappingNullError?,
  fieldName: String,
  innerFields: List<Either<MappingNullError, Any>?> = emptyList(),
): MappingNullError? {
  val innerErrors = buildList {
    innerFields.forEach { innerField ->
      innerField?.onLeft { error -> add(error) }
    }
  }

  if (innerErrors.isEmpty()) return error
  return error
    .getOrDefault(fieldName)
    .copy(errors = innerErrors)
}

private fun <T> checkAllInnerFields(
  kProperty: KProperty1<T, *>,
): MappingNullError? = when {
  kProperty.isBaseType -> checkBaseType(kProperty)
  else -> checkOtherType(kProperty)
}

private fun <T> checkBaseType(
  kProperty: KProperty1<T, *>,
): MappingNullError? {
  if (kProperty.isMarkedNullable) return null
  return MappingNullError(kProperty.name)
}

private fun <T> checkOtherType(
  kProperty: KProperty1<T, *>,
): MappingNullError? {
  if (kProperty.isMarkedNullable) return null
  val innerErrors: List<MappingNullError> = kProperty.returnType.jvmErasure.memberProperties
    .mapNotNull { checkAllInnerFields(it) }
  return MappingNullError(kProperty.name, innerErrors)
}

private val <T> KProperty1<T, *>.isMarkedNullable
  get() = returnType.isMarkedNullable

private val <T> KProperty1<T, *>.isBaseType
  get() = returnType.jvmErasure.let { erasedType ->
    erasedType.isValue
        || erasedType.java.isPrimitive
        || erasedType == String::class
        || Collection::class.java.isAssignableFrom(erasedType.java)
        || Map::class.java.isAssignableFrom(erasedType.java)
  }

fun <T, V : Any?> KProperty1<T, V?>.getNotNull(receiver: T): V {
  return requireNotNull(get(receiver))
}
