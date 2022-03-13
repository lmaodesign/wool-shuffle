package design.lmao.shuffle.runnable.impl

import design.lmao.shuffle.WoolShuffleTask
import design.lmao.shuffle.runnable.DecrementingRunnable
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
object StartingRunnable : DecrementingRunnable(11)
{
    override fun onRun()
    {
        Bukkit.broadcastMessage("${ChatColor.YELLOW}Wool Shuffle is starting in ${ChatColor.RED}$seconds${ChatColor.YELLOW} second${
            if (seconds == 1) "" else "s"
        }!")
    }

    override fun onEnd()
    {
        WoolShuffleTask.run()
    }

    override val secondsAtWhichSomethingHappens: List<Int>
        get() = listOf(10, 5, 4, 3, 2, 1)
}
