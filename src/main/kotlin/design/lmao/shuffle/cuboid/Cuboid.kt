package design.lmao.shuffle.cuboid

import org.bukkit.Location
import org.bukkit.block.Block

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
class Cuboid(
    val first: Location,
    val second: Location
)
{
    val blocks = mutableListOf<Block>()

    init
    {
        // TODO: 3/12/2022 add blocks
    }
}
