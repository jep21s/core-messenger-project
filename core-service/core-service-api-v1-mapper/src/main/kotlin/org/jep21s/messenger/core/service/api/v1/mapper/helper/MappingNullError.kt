package org.jep21s.messenger.core.service.api.v1.mapper.helper

data class MappingNullError(
  val fieldName: String,
  val errors: List<MappingNullError> = emptyList(),
) {
  fun getAllFieldPaths(): List<String> {
    return getAllFieldPaths("")
  }

  private fun getAllFieldPaths(currentPath: String): List<String> {
    val newPath = if (currentPath.isEmpty()) {
      fieldName
    } else if (fieldName.isNotEmpty()) {
      "$currentPath.$fieldName"
    } else {
      currentPath
    }

    return if (errors.isEmpty()) {
      // Листовой узел - возвращаем текущий путь
      listOf(newPath)
    } else {
      // Внутренний узел - собираем пути от всех детей
      errors.flatMap { it.getAllFieldPaths(newPath) }
    }
  }
}

fun MappingNullError?.getOrDefault(
  fieldName: String,
): MappingNullError {
  if (this != null) return this
  return MappingNullError(fieldName)
}
