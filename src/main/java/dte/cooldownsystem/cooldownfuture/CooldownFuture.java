package dte.cooldownsystem.cooldownfuture;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;

/**
 * Represents an action that will happen in the future, that involves a {@code player}'s cooldown.
 * 
 * @see Cooldown
 */
@FunctionalInterface
public interface CooldownFuture
{
	/**
	 * Executes this action on the provided {@code player} and their {@code cooldown}.
	 * 
	 * @param player The uuid of player on cooldown.
	 * @param playerCooldown The player's cooldown.
	 */
	void accept(UUID playerUUID, Cooldown playerCooldown);
	
	

	/**
	 * Creates a {@code CooldownFuture} based on the provided {@code action}, but it runs <b>only</b> if the player is online.
	 * 
	 * @param playerAction The action to run on the online player.
	 * @return The created future.
	 */
	public static CooldownFuture ifOnline(BiConsumer<Player, Cooldown> playerAction) 
	{
		return new OnlinePlayerFuture(playerAction);
	}

	/**
	 * Creates a {@code CooldownFuture} that sends messages to the player, with the following Placeholders:
	 * <nl>
	 * 	<li><i>%time%</i> - the remaining time of the player.
	 * 	<li><i>%player%</i> - the player's name.
	 * </nl>
	 * 
	 * @param messages The messages to send to the player.
	 * @return The created future.
	 */
	public static CooldownFuture message(String... messages) 
	{
		return new MessageFuture(messages);
	}
}