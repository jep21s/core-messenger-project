package org.jep21s.messenger.core.service.repo.cassandra.config.type

import com.datastax.oss.driver.api.core.ProtocolVersion
import com.datastax.oss.driver.api.core.type.DataType
import com.datastax.oss.driver.api.core.type.DataTypes
import com.datastax.oss.driver.api.core.type.codec.TypeCodec
import com.datastax.oss.driver.api.core.type.reflect.GenericType
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.ByteBuffer
import org.jep21s.messenger.core.lib.logging.common.ICMLogWrapper
import org.jep21s.messenger.core.service.common.CSCorSettings

class JsonMapCodec : TypeCodec<Map<String, Any?>?> {
  private val mapper = jacksonObjectMapper()
  private val logger: ICMLogWrapper = CSCorSettings.loggerProvider.logger(this::class)
  private val dataType = DataTypes.TEXT
  private val genericType = GenericType.mapOf(String::class.java, Any::class.java)

  // Возвращаем GenericType для Java типа
  override fun getJavaType(): GenericType<Map<String, Any?>?> = genericType

  // Возвращаем DataType для CQL типа
  override fun getCqlType(): DataType = dataType

  override fun parse(value: String?): Map<String, Any?>? {
    if (value == null) return null

    return try {
      mapper.readValue(value)
    } catch (e: Exception) {
      logger.warn("Failed to parse JSON string: $value", ex = e)
      emptyMap()
    }
  }

  override fun format(value: Map<String, Any?>?): String {
    if (value == null || value.isEmpty()) return "{}"

    return try {
      mapper.writeValueAsString(value)
    } catch (e: Exception) {
      logger.warn("Failed to format map to JSON: $value", ex = e)
      "{}"
    }
  }

  override fun encode(value: Map<String, Any?>?, protocolVersion: ProtocolVersion): ByteBuffer? {
    if (value.isNullOrEmpty()) return null
    val jsonString: String = format(value)
    return ByteBuffer.wrap(jsonString.toByteArray(Charsets.UTF_8))
  }

  override fun decode(bytes: ByteBuffer?, protocolVersion: ProtocolVersion): Map<String, Any?>? {
    if (bytes == null || !bytes.hasRemaining()) return null

    return try {
      val jsonString = String(
        bytes.array(),
        bytes.position(),
        bytes.remaining(),
        Charsets.UTF_8
      )
      parse(jsonString)
    } catch (e: Exception) {
      logger.warn("Failed to decode bytes to map", ex = e)
      null
    }
  }

  override fun accepts(value: Any): Boolean {
    return value is Map<*, *>
  }

  override fun accepts(type: DataType): Boolean {
    return type == dataType
  }
}