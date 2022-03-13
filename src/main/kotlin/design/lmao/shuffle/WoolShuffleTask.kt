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

    private var started = false

    override fun run()
    {
        val runnable = {
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

        if (started)
        {
            Schedulers
                .sync()
                .delay(
                    config.cooldownTime.toLong(),
                    runnable
                )
        } else
        {
            started = true
            runnable.invoke()
        }
    }
}
