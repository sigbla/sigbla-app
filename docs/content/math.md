# Math

In standard Kotlin you can do math between basic primitive types. The result is of the type with the most amount of
precision. An example would be an Int plus a Double will result in a Double.

Compared to Java, standard Kotlin also allows us to use convenience functions when operating on more advanced types
like BigInteger and BigDecimal. This allows us, in standard Kotlin code, to write `BigDecimal.TEN + BigDecimal.ONE`
rather than the more cumbersome `BigDecimal.TEN.add(BigDecimal.ONE)`, like you might be familiar with from Java.

The same applies for other math operations like multiply and divide as well. When doing a `BigDecimal` divide, Kotlin
will use `RoundingMode.HALF_EVEN`.

## Core type extensions

Sigbla extends this further, allowing you to do math between both primitive types (`Int`, `Long`, `Float`, `Double`)
and more advanced types (`BigInteger`, `BigDecimal`). As before, the result is that of the most capable type.

Let's look at an example:

``` kotlin
import sigbla.app.*
import java.math.BigDecimal

fun main() {
    val v1 = BigDecimal.TEN
    val v2 = 100
    val v3 = v1 + v2

    println("v1: $v1 (${v1::class})")
    println("v2: $v2 (${v2::class})")
    println("v3: $v3 (${v3::class})")

    // Output
    // v1: 10 (class java.math.BigDecimal)
    // v2: 100 (class kotlin.Int)
    // v3: 110 (class java.math.BigDecimal)
}
```

The above example would not work without `import sigbla.app.*`.

## Cell type extensions

Because cells might hold values like `Long`, `Double`, `BigInteger` or `BigDecimal`, Sigbla makes it easy to do math
with cells through extensions allowing for all the common math operations. This allows you to take a cell and include
it in any standard mathematical operations:

``` kotlin
import sigbla.app.*
import java.math.BigDecimal

fun main() {
    val table = Table[null]
    table["v1", 0] = BigDecimal.TEN

    val v1 = table["v1", 0]
    val v2 = 100
    val v3 = v1 + v2

    println("v1: $v1 (${v1::class})")
    println("v2: $v2 (${v2::class})")
    println("v3: $v3 (${v3::class})")

    // Output
    // v1: 10 (class sigbla.app.BigDecimalCell)
    // v2: 100 (class kotlin.Int)
    // v3: 110 (class java.math.BigDecimal)
}
```

## Augmented assignments

In the above cell type extensions example we add 100 to a cell. The resulting value can naturally be assigned back to
the cell and done as a single line like so:

`table["v1", 0] = table["v1", 0] + 100`

A shorthand version of this is:

`table["v1", 0] += 100`

They are functionally exactly the same thing, with the latter being a bit more convenient. Other augmented assignment
operators are also available: `-=`, `*=`, `/=`, and `%=`.

## Precision and rounding modes

When converting a number to the `BigDecimal` type a `MathContext` is included. This context is also used when performing
divisions by passing in its rounding mode.

The math context used is something you can control with `Math[Precision]` as shown next. By default, this is set to
`MathContext.DECIMAL64`, which uses a precision of 16 and a rounding mode of half even.

``` kotlin
import sigbla.app.*
import java.math.MathContext

fun main() {
    val table = Table[null]
    table["v", 0] = 10

    // This sets precision to zero, which means it will not limit
    // the precision of a given number when converting it
    Math[Precision] = MathContext.UNLIMITED
    val v1 = table["v", 0].asBigDecimal

    println("Math[Precision].precision = " + Math[Precision].precision)
    println("v1 precision: " + v1!!.precision())
    
    // Output:
    // Math[Precision].precision = 0
    // v1 precision: 2

    // Forces precision to be 1, which will impact
    // our number when converting it
    Math[Precision] = MathContext(1)
    val v2 = table["v", 0].asBigDecimal

    println("Math[Precision].precision = " + Math[Precision].precision)
    println("v2 precision: " + v2!!.precision())
    
    // Output:
    // Math[Precision].precision = 1
    // v2 precision: 1
}
```

Next example shows the rounding mode impact on divisions:

``` kotlin
import sigbla.app.*
import java.math.BigDecimal
import java.math.MathContext
import java.math.RoundingMode

fun main() {
    val table = Table[null]
    table["v", 0] = 1000

    Math[Precision] = MathContext(0, RoundingMode.CEILING)

    val v1 = table["v", 0] / BigDecimal.valueOf(3)
    println(v1)

    // Output:
    // 334

    Math[Precision] = MathContext(0, RoundingMode.FLOOR)

    val v2 = table["v", 0] / BigDecimal.valueOf(3)
    println(v2)

    // Output:
    // 333
}
```

Note that once the value on a particular cell instance has been converted to a big decimal value, like what happens
above when doing the divide, the particular big decimal value is cached within the cell instance. Hence, when changing
the `Math[Precision]` value, we need to obtain a new cell instance for this to take effect.
