package design.lmao.shuffle

import design.lmao.shuffle.cuboid.Cuboid
import design.lmao.shuffle.runnable.impl.StartingRunnable
import design.lmao.shuffle.util.Listener
import design.lmao.shuffle.util.Schedulers
import design.lmao.shuffle.wool.WoolColors
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Configure
import gg.scala.flavor.service.Service
import org.bukkit.Bukkit
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.FoodLevelChangeEvent
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.ItemStack
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
    lateinit var color: ChatColor

    var started = false
    var round = 1

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
            .listenTo<EntityDamageEvent>()
            .apply(plugin)
            .filter { it.cause == EntityDamageEvent.DamageCause.FALL }
            .cancelOn { true }

        Listener
            .listenTo<EntityDamageByEntityEvent>()
            .apply(plugin)
            .filter { it.entity is Player }
            .filter { it.damager is Player && it.damager.hasMetadata("spectator") }
            .cancelOn { started }

        listOf(
            BlockPlaceEvent::class,
            BlockBreakEvent::class,
            FoodLevelChangeEvent::class,
            InventoryClickEvent::class
        ).forEach {
            Listener
                .listenTo(it.java)
                .apply(plugin)
                .cancelOn {
                    started
                }
        }

        Listener
            .listenTo<PlayerMoveEvent>()
            .apply(plugin)
            .filter { it.to.y <= config.minimumYLevel }
            .filter { !it.player.hasMetadata("spectator") }
            .on {
                disqualify(it.player, false)
            }

        Listener
            .listenTo<PlayerJoinEvent>()
            .apply(plugin)
            .on {
                val player = it.player
                it.joinMessage = "${player.name}${ChatColor.YELLOW} joined!"

                player.reset()

                player.sendMessage("${ChatColor.GREEN}Welcome to ${ChatColor.BOLD}Wool Shuffle${ChatColor.GREEN}!")
                player.teleport(config.spawnCoordinate)

                if (started || plugin.config.maxPlayers < Bukkit.getOnlinePlayers().size)
                {
                    disqualify(player, true)
                } else if (!started && Bukkit.getOnlinePlayers().size >= config.minPlayers)
                {
                    start()
                }
            }
    }

    fun Player.reset()
    {
        inventory.clear()

        health = 20.0
        foodLevel = 20

        player.removeMetadata("spectator", plugin)
    }

    fun reboot(player: Player)
    {
        started = false

        // set all blocks to wool
        cuboid.forEach {
            val block = it.block

            block.data = 0
            block.type = Material.WOOL
        }

        Bukkit.getOnlinePlayers().forEach {
            Bukkit.getOnlinePlayers().forEach { other ->
                other.showPlayer(it)
                it.showPlayer(other)
            }

            it.reset()
            it.teleport(plugin.config.spawnCoordinate)
        }

        Bukkit.broadcastMessage("${ChatColor.GREEN}The game is over! ${ChatColor.GOLD}${player.name}${ChatColor.GREEN} won!")
    }

    fun start()
    {
        started = true

        Schedulers
            .sync()
            .repeating(0L, 20L) {
                StartingRunnable.run()
            }
    }

    fun spectate(player: Player)
    {
        player.reset()

        player.allowFlight = true
        player.isFlying = true

        player.teleport(
            plugin.config.spawnCoordinate
        )

        for (other in Bukkit.getOnlinePlayers())
        {
            if (other == player)
                continue

            if (!other.hasMetadata("spectator"))
            {
                other.hidePlayer(player)
            }
        }

        player.setMetadata(
            "spectator",
            FixedMetadataValue(
                plugin, ""
            )
        )
    }

    fun disqualify(player: Player, lateConnect: Boolean)
    {
        spectate(player)

        player.sendMessage(
            "${ChatColor.RED}You've been disqualified!"
        )

        Bukkit.broadcastMessage("${player.name}${ChatColor.YELLOW} was disqualified.")

        if (lateConnect)
        {
            player.sendMessage(
                "${ChatColor.GRAY} You joined too late into the game!"
            )
        }
    }

    /**
     * Shuffle wool colors for all blocks in the [cuboid].
     */
    fun shuffle(
        color: ChatColor =
            WoolColors.availableColors.random()
    )
    {
        this.color = color

        cuboid
            .map { it.block }
            .forEach {
                it.type = Material.WOOL
                it.data = WoolColors.randomColor()
            }

        val wool = ItemStack(
            Material.WOOL, 1,
            WoolColors
                .fromChatColor(color)
                .toShort()
        ).apply {
            val itemMeta = this.itemMeta
            itemMeta.displayName = "$color${color.better()} Wool"
            itemMeta.lore = mutableListOf(
                "${ChatColor.GRAY}Stand on this block!"
            )

            this.itemMeta = itemMeta
        }

        val players = Bukkit.getOnlinePlayers()
            .filter { !it.hasMetadata("spectator") }

        for (player in players)
        {
            for (i in 0..8)
            {
                player.inventory.setItem(i, wool)
            }
        }

        Bukkit.broadcastMessage(
            "${ChatColor.YELLOW}The wool has been shuffled! Run to a $color${color.better()}${ChatColor.YELLOW} block!"
        )
    }

    /**
     * Eliminate players who are not on
     * a block with the color [color].
     */
    fun eliminate()
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
            "${ChatColor.YELLOW}Players who were not on $color${color.better()}${ChatColor.YELLOW} wool were ${ChatColor.RED}eliminated${ChatColor.YELLOW}!"
        )

        val players = Bukkit.getOnlinePlayers()
            .filter { !it.hasMetadata("spectator") }

        Schedulers.sync()
            .delay(20L) {
                if (players.size == 1)
                {
                    reboot(players[0])
                } else
                {
                    WoolShuffleTask.run()
                }
            }
    }
}

fun ChatColor.better(): String = name
    .replace("_", " ")
    .lowercase().capitalize()
