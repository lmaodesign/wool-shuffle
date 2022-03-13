package design.lmao.shuffle

import design.lmao.shuffle.util.Schedulers
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service

@Service
object WoolShuffleTask : Thread()
{
    @Inject
    lateinit var plugin: WoolShuffle

    private val config by lazy {
        plugin.config
    }

    override fun run()
    {
        while (true)
        {
            Schedulers
                .sync()
                .call {
                    WoolShuffleHandler.eliminate()
                }

            sleep(config.cooldownTime * 50L)

            Schedulers
                .sync()
                .call {
                    WoolShuffleHandler.shuffle()
                }

            sleep((config.shuffleDelay + (WoolShuffleHandler.round * config.shuffleDelayMultiplier)).toLong())
        }
    }
}