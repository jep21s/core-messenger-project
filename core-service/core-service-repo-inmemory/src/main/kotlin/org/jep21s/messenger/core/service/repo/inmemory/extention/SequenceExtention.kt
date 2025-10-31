package org.jep21s.messenger.core.service.repo.inmemory.extention

fun <T, F> Sequence<T>.doFilterIfNotNull(
  field: F?,
  predicate: (F) -> ((T) -> Boolean),
): Sequence<T> {
  if (field == null) return this
  return filter(predicate(field))
}

fun <T, F> Sequence<T>.doFilterIfNotNull(
  fields: List<F>?,
  predicate: (List<F>) -> ((T) -> Boolean),
): Sequence<T> {
  if (fields.isNullOrEmpty()) return this
  return filter(predicate(fields))
}