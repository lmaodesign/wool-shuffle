package design.lmao.shuffle.util

import design.lmao.shuffle.WoolShuffle
import gg.scala.flavor.inject.Inject
import gg.scala.flavor.service.Service
import org.bukkit.scheduler.BukkitScheduler

@Service
object Schedulers
{
    @Inject
    lateinit var plugin: WoolShuffle

    internal val scheduler: BukkitScheduler by lazy {
        plugin.server.scheduler
    }

    fun sync(): SyncScheduler
    {
        return SyncScheduler
    }

    fun async(): AsyncScheduler
    {
        return AsyncScheduler
    }
}

interface Scheduler
{
    /**
     * Run a repeating task
     *
     * @param delay the delay before starting the first execution
     * @param period the amount of time between execution
     * @param action the action to execute
     */
    fun repeating(delay: Long = 0L, period: Long, action: () -> Unit)

    /**
     * Execute an action after a certain delay
     *
     * @param delay the amount of time it takes before executing the action
     * @param action the action to execute
     */
    fun delay(delay: Long, action: () -> Unit)

    /**
     * Call in the context of the current scheduler
     *
     * @param action the action to execute
     */
    fun call(action: () -> Unit)
}

object SyncScheduler : Scheduler
{
    override fun repeating(delay: Long, period: Long, action: () -> Unit)
    {
        Schedulers.scheduler.scheduleSyncRepeatingTask(
            Schedulers.plugin,
            action,
            delay,
            period
        )
    }

    override fun delay(delay: Long, action: () -> Unit)
    {
        Schedulers.scheduler.runTaskLater(Schedulers.plugin, action, delay)
    }

    override fun call(action: () -> Unit)
    {
        Schedulers.scheduler.runTask(Schedulers.plugin, action)
    }
}

object AsyncScheduler : Scheduler
{
    override fun repeating(delay: Long, period: Long, action: () -> Unit)
    {
        Schedulers.scheduler.scheduleAsyncRepeatingTask(
            Schedulers.plugin,
            action,
            delay,
            period
        )
    }

    override fun delay(delay: Long, action: () -> Unit)
    {
        Schedulers.scheduler.runTaskLaterAsynchronously(Schedulers.plugin, action, delay)
    }

    override fun call(action: () -> Unit)
    {
        Schedulers.scheduler.runTaskAsynchronously(Schedulers.plugin, action)
    }
}