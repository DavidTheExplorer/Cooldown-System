package dte.cooldownsystem.cooldown.future;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;

/**
 * Represents a future action that handles a player and their cooldown.
 */
@FunctionalInterface
public interface CooldownFuture
{
	/**
	 * Executes on the provided {@code player}(identified by their UUID) and their {@code cooldown}.
	 * 
	 * @param playerUUID The uuid of player.
	 * @param playerCooldown The player's cooldown.
	 */
	void accept(UUID playerUUID, Cooldown playerCooldown);
	
	

	/**
	 * Creates a future based on the provided {@code action} that runs only if the player is online.
	 * 
	 * @param playerAction The delegate action.
	 * @return The created future.
	 */
	static CooldownFuture ifOnline(BiConsumer<Player, Cooldown> playerAction)
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
	static CooldownFuture message(String... messages)
	{
		return new MessageFuture(messages);
	}
}