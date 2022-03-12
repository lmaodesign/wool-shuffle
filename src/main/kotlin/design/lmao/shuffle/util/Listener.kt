package design.lmao.shuffle.util

import org.bukkit.event.Cancellable
import org.bukkit.event.Event
import org.bukkit.event.EventPriority
import org.bukkit.plugin.java.JavaPlugin

object Listener
{
    inline fun <reified T : Event> listenTo() : ListenerBuilder<T>
    {
        return ListenerBuilder(T::class.java)
    }
}

class ListenerBuilder<T : Event>(private val type: Class<T>)
{
    private val filters = mutableListOf<Filter<T>>()
    private val cancelOn = mutableListOf<Filter<T>>()
    private val handle = mutableListOf<HandleEvent<T>>()

    private var priority: EventPriority = EventPriority.NORMAL

    fun filter(filter: Filter<T>): ListenerBuilder<T>
    {
        return this.apply {
            this.filters.add(filter)
        }
    }

    fun cancelOn(filter: Filter<T>): ListenerBuilder<T>
    {
        return this.apply {
            this.cancelOn.add(filter)
        }
    }

    fun priority(priority: EventPriority): ListenerBuilder<T>
    {
        return this.apply {
            this.priority = priority
        }
    }

    fun on(handle: HandleEvent<T>): ListenerBuilder<T>
    {
        return this.apply {
            this.handle.add(handle)
        }
    }

    fun apply(plugin: JavaPlugin): ListenerBuilder<T>
    {
        return this.apply {
            val pluginManager = plugin.server.pluginManager
            val listener = object : org.bukkit.event.Listener
            {}

            pluginManager.registerEvent(
                type,
                listener,
                priority,
                { _, event ->
                    for (filter in filters)
                    {
                        if (!filter.invoke(event as T))
                        {
                            return@registerEvent
                        }
                    }

                    for (function in cancelOn)
                    {
                        if (function.invoke(event as T) && event is Cancellable)
                        {
                            event.isCancelled = true
                        }
                    }

                    for (function in handle)
                    {
                        function.invoke(event as T)
                    }
                },
                plugin
            )
        }
    }
}

typealias Filter<T> = (T) -> Boolean
typealias HandleEvent<T> = (T) -> Unit