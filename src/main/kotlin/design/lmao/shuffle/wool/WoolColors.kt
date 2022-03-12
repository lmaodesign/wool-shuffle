package design.lmao.shuffle.wool

import org.bukkit.ChatColor

/**
 * @author GrowlyX
 * @since 3/12/2022
 */
object WoolColors
{
    val availableColors = ChatColor.values()
        .filter {
            it.isColor
        }

    private val mappings = mapOf(
        ChatColor.DARK_RED to 14,
        ChatColor.RED to 14,
        ChatColor.GOLD to 1,
        ChatColor.YELLOW to 4,
        ChatColor.GREEN to 5,
        ChatColor.DARK_GREEN to 13,
        ChatColor.DARK_AQUA to 9,
        ChatColor.AQUA to 3,
        ChatColor.BLUE to 11,
        ChatColor.DARK_PURPLE to 10,
        ChatColor.LIGHT_PURPLE to 2,
        ChatColor.WHITE to 0,
        ChatColor.GRAY to 8,
        ChatColor.DARK_GRAY to 7,
        ChatColor.BLACK to 15
    )

    fun fromChatColor(
        color: ChatColor
    ): Byte
    {
        return mappings[color]?.toByte() ?: 0
    }
}
