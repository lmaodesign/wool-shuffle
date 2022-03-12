package design.lmao.shuffle.cuboid

import design.lmao.shuffle.util.NumberUtil
import org.bukkit.Location

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
class Cuboid(
    first: Location,
    second: Location
) : Iterable<Location>
{
    val blocks = mutableListOf<Location>()

    init
    {
        if (first.y != second.y)
        {
            throw IllegalArgumentException("first.y is not equal to second.y")
        }

        if (first.world != second.world)
        {
            throw IllegalArgumentException("first.world is not equal to second.world")
        }

        val rangeX = NumberUtil.findMinMaxPair(first.x, second.x)
        val rangeZ = NumberUtil.findMinMaxPair(first.z, second.z)

        for (x in rangeX)
        {
            for (z in rangeZ)
            {
                blocks.add(
                    Location(
                        first.world,
                        x.toDouble(),
                        first.y,
                        z.toDouble()
                    )
                )
            }
        }
    }

    override fun iterator(): Iterator<Location>
    {
        return CuboidIterator(this)
    }
}

class CuboidIterator(private val cuboid: Cuboid) : Iterator<Location>
{
    private var currentIndex = 0

    override fun hasNext(): Boolean
    {
        return cuboid.blocks.size > currentIndex
    }

    override fun next(): Location
    {
        return cuboid.blocks[currentIndex++]
    }
}
