package design.lmao.shuffle

import design.lmao.shuffle.util.Listener
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.Bukkit
import org.bukkit.event.player.PlayerJoinEvent
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

    @Configure
    fun configure()
    {
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
}