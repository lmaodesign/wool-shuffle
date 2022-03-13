package design.lmao.shuffle.command

import design.lmao.shuffle.WoolShuffleHandler
import org.bukkit.ChatColor
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender

object ShuffleCommand : CommandExecutor
{
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean
    {
        if (args.isEmpty())
        {
            sender.sendMessage("usage: /shuffle start")
            return true
        }

        when (args[0])
        {
            "start" ->
            {
                if (WoolShuffleHandler.started)
                {
                    sender.sendMessage("${ChatColor.RED}Sorry, the game already started!")
                    return true
                }

                WoolShuffleHandler.start()
            }
            else -> sender.sendMessage("usage: /shuffle start")
        }

        return true
    }
}
