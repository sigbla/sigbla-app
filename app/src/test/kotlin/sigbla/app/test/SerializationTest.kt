/* Copyright 2019-2024, Christian Felde.
 * See LICENSE file for licensing details. */
package sigbla.app.test

import org.junit.Assert.*
import org.junit.Test
import sigbla.app.internals.SerializationType
import sigbla.app.internals.SerializationUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.util.concurrent.ThreadLocalRandom

class SerializationTest {
    @Test
    fun `type serialization`() {
        val check = mutableSetOf<Int>()
        for (i in 0..3500000) {
            val rnd = SerializationType.values().map { it.type }.toList().shuffled().first()
            check.add(rnd)

            when (rnd) {
                0 -> {
                    assertEquals(0, SerializationType.NULL.type)
                    assertNull(SerializationUtils.toType(SerializationUtils.fromType(null)))
                }
                1 -> {
                    assertEquals(1, SerializationType.BOOL.type)
                    val v1 = ThreadLocalRandom.current().nextBoolean()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Boolean", v2?.javaClass.toString())
                }
                2 -> {
                    assertEquals(2, SerializationType.BYTE.type)
                    val v1 = ThreadLocalRandom.current().nextInt().toByte()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Byte", v2?.javaClass.toString())
                }
                3 -> {
                    assertFalse(true)
                }
                4 -> {
                    assertEquals(4, SerializationType.INT.type)
                    val v1 = ThreadLocalRandom.current().nextInt()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Integer", v2?.javaClass.toString())
                }
                5 -> {
                    assertEquals(5, SerializationType.LONG.type)
                    val v1 = ThreadLocalRandom.current().nextLong()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Long", v2?.javaClass.toString())
                }
                6 -> {
                    assertFalse(true)
                }
                7 -> {
                    assertEquals(7, SerializationType.DOUBLE.type)
                    val v1 = ThreadLocalRandom.current().nextDouble()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Double", v2?.javaClass.toString())
                }
                8 -> {
                    assertFalse(true)
                }
                9 -> {
                    assertEquals(9, SerializationType.STRING.type)
                    val ba = ByteArray(ThreadLocalRandom.current().nextInt(0, 1000))
                    ThreadLocalRandom.current().nextBytes(ba)
                    val v1 = String(ba)
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                10 -> {
                    assertEquals(10, SerializationType.BIGINTEGER.type)
                    val v1 = BigInteger(ThreadLocalRandom.current().nextLong().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, 100))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                11 -> {
                    assertEquals(11, SerializationType.BIGDECIMAL.type)
                    val v1 = BigDecimal(ThreadLocalRandom.current().nextDouble().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                12 -> {
                    assertEquals(12, SerializationType.LOCALDATE.type)
                    val v1 = LocalDate.of(ThreadLocalRandom.current().nextInt(-999999999, 1_000_000_000), ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(1, 28))
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                13 -> {
                    assertEquals(13, SerializationType.LOCALTIME.type)
                    val v1 = LocalTime.of(ThreadLocalRandom.current().nextInt(0, 24), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 1_000_000_000))
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                14 -> {
                    assertEquals(14, SerializationType.LOCALDATETIME.type)
                    val localDate = LocalDate.of(ThreadLocalRandom.current().nextInt(-999999999, 1_000_000_000), ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(1, 28))
                    val localTime = LocalTime.of(ThreadLocalRandom.current().nextInt(0, 24), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 1_000_000_000))
                    val v1 = LocalDateTime.of(localDate, localTime)
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                15 -> {
                    assertEquals(15, SerializationType.ZONEDDATETIME.type)
                    val localDate = LocalDate.of(ThreadLocalRandom.current().nextInt(-999999999, 1_000_000_000), ThreadLocalRandom.current().nextInt(1, 12), ThreadLocalRandom.current().nextInt(1, 28))
                    val localTime = LocalTime.of(ThreadLocalRandom.current().nextInt(0, 24), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 60), ThreadLocalRandom.current().nextInt(0, 1_000_000_000))
                    val v1 = ZonedDateTime.of(localDate, localTime, ZoneId.of(ZoneId.getAvailableZoneIds().shuffled().first()))
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                16 -> {
                    assertFalse(true)
                }
                17 -> {
                    assertFalse(true)
                }
                18 -> {
                    assertFalse(true)
                }
                19 -> {
                    assertFalse(true)
                }
                20 -> {
                    // Practically same as string but with different type value
                    assertEquals(20, SerializationType.WEBCONTENT.type)
                    val ba = ByteArray(ThreadLocalRandom.current().nextInt(0, 1000))
                    ThreadLocalRandom.current().nextBytes(ba)
                    val v1 = String(ba)
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                else -> throw UnsupportedOperationException()
            }
        }
        assertEquals(SerializationType.entries.size, check.size)
    }
}
