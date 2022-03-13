package design.lmao.shuffle

import design.lmao.shuffle.runnable.impl.EliminationRunnable
import design.lmao.shuffle.util.Schedulers
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service

@Service
object WoolShuffleTask : Runnable
{
    @Inject
    lateinit var plugin: WoolShuffle

    private val config by lazy {
        plugin.config
    }

    override fun run()
    {
        Schedulers
            .sync()
            .delay(config.cooldownTime.toLong()) {
                WoolShuffleHandler.shuffle()

                val runnable = EliminationRunnable(
                    config.shuffleDelay - (WoolShuffleHandler.round * config.shuffleDelayMultiplier)
                )

                Schedulers
                    .sync()
                    .repeating(0L, 20L) {
                        runnable.run()
                    }
            }
    }
}
