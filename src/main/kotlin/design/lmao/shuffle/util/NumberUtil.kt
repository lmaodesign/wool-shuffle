package design.lmao.shuffle.util

/**
 * @author string
 * @since 3/12/2022
 */
object NumberUtil
{
    fun findMinMaxPair(num1: Long, num2: Long): LongRange
    {
        val max = num1.coerceAtLeast(num2)
        val min = num1.coerceAtMost(num2)

        return min..max
    }

    fun findMinMaxPair(num1: Double, num2: Double): LongRange
    {
        return this.findMinMaxPair(num1.toLong(), num2.toLong())
    }
}