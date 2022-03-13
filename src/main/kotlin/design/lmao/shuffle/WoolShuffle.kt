package design.lmao.shuffle

import design.lmao.shuffle.command.ShuffleCommand
import gg.scala.flavor.Flavor
import gg.scala.flavor.FlavorOptions
import org.bukkit.plugin.java.JavaPlugin
import xyz.mkotb.configapi.ConfigFactory

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
class WoolShuffle : JavaPlugin()
{
    companion object
    {
        @JvmStatic
        lateinit var INSTANCE: WoolShuffle
    }

    private val flavor by lazy {
        Flavor.create<WoolShuffle>(
            FlavorOptions(logger)
        )
    }

    private val factory by lazy {
        ConfigFactory.newFactory(this)
    }

    lateinit var config: WoolShuffleConfig

    override fun onEnable()
    {
        INSTANCE = this

        config = factory.fromFile(
            "settings", WoolShuffleConfig::class.java
        )

        flavor.bind<WoolShuffle>() to this
        flavor.startup()

        this.getCommand("shuffle")
            .executor = ShuffleCommand
    }

    override fun onDisable()
    {
        flavor.close()
    }
}
