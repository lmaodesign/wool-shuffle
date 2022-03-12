package design.lmao.shuffle

import org.bukkit.Bukkit
import org.bukkit.Location
import xyz.mkotb.configapi.comment.Comment

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
class WoolShuffleConfig
{
    val maxPlayers = 20
    val minPlayers = 20

    @Comment("The arena coordinates.")
    val arenaCoordinates =
        mutableListOf(
            Location(Bukkit.getWorld("world"), 0.0, 32.0, 0.0),
            Location(Bukkit.getWorld("world"), 10.0, 32.0, 10.0)
        )

    @Comment("The delay at which the wool shuffles (in ticks).")
    val shuffleDelay = 100

    @Comment("How much the delay decrements by each shuffle.")
    val shuffleDelayMultiplier = 0.6
}
