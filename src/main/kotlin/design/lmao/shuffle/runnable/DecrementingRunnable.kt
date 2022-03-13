package design.lmao.shuffle.runnable

import org.bukkit.scheduler.BukkitRunnable

/**
 * @author puugz
 * @since 23/08/2021 19:19
 */
abstract class DecrementingRunnable(
    internal var seconds: Int
) : BukkitRunnable()
{
    override fun run()
    {
        seconds--

        val shouldSomethingHappen =
            secondsAtWhichSomethingHappens.contains(seconds)

        if (shouldSomethingHappen)
        {
            onRun()
        } else if (seconds == 0)
        {
            onEnd()
            cancel()
        }
    }

    abstract fun onRun()
    abstract fun onEnd()

    abstract val secondsAtWhichSomethingHappens: List<Int>
}
