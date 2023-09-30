package sigbla.app.test

import org.junit.Assert.*
import org.junit.Test
import sigbla.app.internals.SerializationType
import sigbla.app.internals.SerializationUtils
import java.math.BigDecimal
import java.math.BigInteger
import java.util.concurrent.ThreadLocalRandom

class SerializationTest {
    @Test
    fun `type serialization`() {
        val check = mutableSetOf<Int>()
        for (i in 0..2500000) {
            val rnd = SerializationType.values().map { it.type }.toList().shuffled().first()
            check.add(rnd)

            when (rnd) {
                0 -> {
                    assertNull(SerializationUtils.toType(SerializationUtils.fromType(null)))
                }
                1 -> {
                    val v1 = ThreadLocalRandom.current().nextBoolean()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Boolean", v2?.javaClass.toString())
                }
                2 -> {
                    val v1 = ThreadLocalRandom.current().nextInt().toByte()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Byte", v2?.javaClass.toString())
                }
                3 -> {
                    assertFalse(true)
                }
                4 -> {
                    val v1 = ThreadLocalRandom.current().nextInt()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Integer", v2?.javaClass.toString())
                }
                5 -> {
                    val v1 = ThreadLocalRandom.current().nextLong()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Long", v2?.javaClass.toString())
                }
                6 -> {
                    assertFalse(true)
                }
                7 -> {
                    val v1 = ThreadLocalRandom.current().nextDouble()
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals("class java.lang.Double", v2?.javaClass.toString())
                }
                8 -> {
                    assertFalse(true)
                }
                9 -> {
                    val ba = ByteArray(ThreadLocalRandom.current().nextInt(0, 1000))
                    ThreadLocalRandom.current().nextBytes(ba)
                    val v1 = String(ba)
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                10 -> {
                    val v1 = BigInteger(ThreadLocalRandom.current().nextLong().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigInteger.valueOf(ThreadLocalRandom.current().nextLong(1, 100))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                11 -> {
                    val v1 = BigDecimal(ThreadLocalRandom.current().nextDouble().toString()).let {
                        if (ThreadLocalRandom.current().nextBoolean()) it.multiply(BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(1.0, 100.0))) else it
                    }
                    val v2 = SerializationUtils.toType(SerializationUtils.fromType(v1))
                    assertEquals(v1, v2)
                    assertEquals(v1.javaClass.toString(), v2?.javaClass.toString())
                }
                else -> throw UnsupportedOperationException()
            }
        }
        assertEquals(SerializationType.values().size, check.size)
    }
}