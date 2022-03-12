package design.lmao.shuffle.cuboid

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

        val minX = first.x.coerceAtMost(second.x).toInt()
        val minZ = first.z.coerceAtMost(second.z).toInt()

        val maxX = first.x.coerceAtLeast(second.x).toInt()
        val maxZ = first.z.coerceAtLeast(second.z).toInt()

        for (x in minX..maxX)
        {
            for (z in minZ..maxZ)
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

class CuboidIterator(val cuboid: Cuboid) : Iterator<Location>
{
    var currentIndex = 0

    override fun hasNext(): Boolean
    {
        return cuboid.blocks.size > currentIndex
    }

    override fun next(): Location
    {
        return cuboid.blocks[currentIndex++]
    }
}