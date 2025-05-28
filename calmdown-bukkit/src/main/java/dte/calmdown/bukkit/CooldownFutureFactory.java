package dte.calmdown.bukkit;

import dte.calmdown.Cooldown;
import dte.calmdown.CooldownFuture;
import dte.calmdown.bukkit.futures.MessageFuture;
import dte.calmdown.bukkit.futures.OnlinePlayerFuture;
import org.bukkit.entity.Player;

import java.util.function.BiConsumer;

public class CooldownFutureFactory
{
    /**
     * Creates a future based on the provided {@code action} that runs only if the player is online.
     *
     * @param playerAction The delegate action.
     * @return The created future.
     */
    public static CooldownFuture<Player> ifOnline(BiConsumer<Player, Cooldown<Player>> playerAction)
    {
        return new OnlinePlayerFuture(playerAction);
    }

    /**
     * Creates a future that messages the player, with the following placeholders:
     * <nl>
     * 	<li><i>%time%</i> - the remaining time of the player.
     * 	<li><i>%player%</i> - the player's name.
     * </nl>
     *
     * @param messages The messages to send to the player.
     * @return The created future.
     */
    public static CooldownFuture<Player> message(String... messages)
    {
        return new MessageFuture(messages);
    }
}
