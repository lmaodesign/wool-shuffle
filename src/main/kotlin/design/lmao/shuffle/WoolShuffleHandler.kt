package design.lmao.shuffle

import design.lmao.shuffle.util.Listener
import design.lmao.shuffle.cuboid.Cuboid
import design.lmao.shuffle.wool.WoolColors
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.metadata.FixedMetadataValue

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
@Service
object WoolShuffleHandler
{
    @Inject
    lateinit var plugin: WoolShuffle

    private lateinit var cuboid: Cuboid

    @Configure
    fun configure()
    {
        val config = plugin.config

        val minimum = config
            .arenaCoordinates[0]
        val maximum = config
            .arenaCoordinates[1]

        cuboid = Cuboid(minimum, maximum)

        // set all blocks to wool
        cuboid.forEach {
            val block = it.block

            block.data = 0
            block.type = Material.WOOL
        }

        Listener
            .listenTo<PlayerMoveEvent>()
            .filter { it.to.y <= config.minimumYLevel }
            .filter { !it.player.hasMetadata("spectator") }
            .on {
                disqualify(it.player)
            }

        Listener
            .listenTo<PlayerJoinEvent>()
            .apply(plugin)
            .on {
                val player = it.player

                // TODO: 3/12/22 check if match has been started

                if (plugin.config.maxPlayers >= Bukkit.getOnlinePlayers().size + 1
                    && !player.hasPermission("shuffle.bypass")
                )
                {
                    player.setMetadata(
                        "spectator",
                        FixedMetadataValue(
                            plugin,
                            ""
                        )
                    )
                    // TODO: 3/12/22 set player to spectator
                }
            }
    }

    fun disqualify(player: Player)
    {
        player.allowFlight = true
        player.isFlying = true

        player.teleport(
            plugin.config.spawnCoordinate
        )

        for (other in Bukkit.getOnlinePlayers())
        {
            if (other != player || !other.hasMetadata("spectator"))
            {
                player.hidePlayer(other)
            }
        }

        player.sendMessage(
            "${ChatColor.DARK_RED}You've been disqualified!"
        )
    }

    /**
     * Shuffle wool colors for all blocks in the [cuboid].
     */
    fun shuffle(color: ChatColor)
    {
        cuboid.forEach {
            val block = it.block
            val randomColor = WoolColors
                .availableColors
                .random()

            block.data = WoolColors
                .fromChatColor(randomColor)
        }

        Bukkit.broadcastMessage(
            "${ChatColor.YELLOW}The wool has been shuffled! Run to a $color${color.name}${ChatColor.YELLOW} block!"
        )
    }

    /**
     * Eliminate players who are not on
     * a block with the color [color].
     */
    fun eliminate(color: ChatColor)
    {
        val woolData = WoolColors
            .fromChatColor(color)

        cuboid
            .map { it.block }
            .filter { it.data != woolData }
            .forEach {
                it.type = Material.AIR
            }

        Bukkit.broadcastMessage(
            "${ChatColor.YELLOW}Players who were not on $color${color.name}${ChatColor.YELLOW} wool were ${ChatColor.RED}ELIMINATED${ChatColor.YELLOW}!"
        )
    }
}
