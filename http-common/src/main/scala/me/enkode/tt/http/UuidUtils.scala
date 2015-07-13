package me.enkode.tt.http

import java.nio.ByteBuffer
import java.util.{Base64, UUID}

import spray.json.JsString

trait UuidUtils {
  val longBytes = java.lang.Long.BYTES

  def fromBytes(bytes: Array[Byte]): Long = {
    val buffer = ByteBuffer.allocate(longBytes)
    buffer.put(bytes)
    buffer.flip()
    buffer.getLong
  }

  def toBytes(x: Long): Array[Byte] = {
    val buffer = ByteBuffer.allocate(longBytes)
    buffer.putLong(x).array()
  }

  def toBase64(uuid: UUID): String = {
    val (high, low) = (uuid.getMostSignificantBits, uuid.getLeastSignificantBits)
    val buffer = ByteBuffer.allocate(longBytes * 2)
    buffer.putLong(high)
    buffer.putLong(low)
    val encoded = Base64.getMimeEncoder.encodeToString(buffer.array())
    encoded.take(encoded.length - 2)
  }

  def fromBase64(b64: String): UUID = {
    val bytes = Base64.getMimeDecoder.decode(b64)
    assert(bytes.length == 16, "uuids must be 16 bytes long")
    val (high, low) = bytes.splitAt(8)
    new UUID(fromBytes(high), fromBytes(low))
  }
}

object UuidUtils extends UuidUtils
