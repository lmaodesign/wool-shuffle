package design.lmao.shuffle.runnable.impl

import design.lmao.shuffle.WoolShuffleHandler
import design.lmao.shuffle.runnable.DecrementingRunnable
import org.bukkit.Bukkit
import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
class EliminationRunnable(
    seconds: Double
) : DecrementingRunnable(seconds.toInt())
{
    override fun onRun()
    {
        Bukkit.broadcastMessage("${ChatColor.YELLOW}Players will be eliminated in ${ChatColor.RED}$seconds${ChatColor.YELLOW} second${
            if (seconds == 1) "" else "s"
        }!")
    }

    override fun onEnd()
    {
        WoolShuffleHandler.eliminate()
    }

    override val secondsAtWhichSomethingHappens: List<Int>
        get() = listOf(18000, 14400, 10800, 7200, 3600, 2700, 1800, 900, 600, 300, 240, 180, 120, 60, 50, 40, 30, 15, 10, 5, 4, 3, 2, 1)
}
