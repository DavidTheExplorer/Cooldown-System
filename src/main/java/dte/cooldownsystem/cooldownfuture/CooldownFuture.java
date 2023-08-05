package dte.cooldownsystem.cooldownfuture;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.utils.DurationUtils;

/**
 * Represents an action that will happen in the future, that involves a {@code player}'s cooldown.
 * <p>
 * Usually set while creating a new Cooldown object.
 */
@FunctionalInterface
public interface CooldownFuture extends BiConsumer<UUID, Cooldown>
{
	/**
	 * Executes this action on the provided {@code player} and their {@code cooldown}.
	 * 
	 * @param player The uuid of player on cooldown.
	 * @param playerCooldown The player's cooldown.
	 */
	@Override
	void accept(UUID playerUUID, Cooldown playerCooldown);
	
	

	/**
	 * Creates a {@code CooldownFuture} based on the provided {@code action}, but it runs <b>only</b> if the player is online.
	 * 
	 * @param playerAction The action to run on the online player.
	 * @return The created future.
	 */
	public static CooldownFuture ifOnline(BiConsumer<Player, Cooldown> playerAction) 
	{
		return (playerUUID, playerCooldown) -> 
		{
			Player player = Bukkit.getPlayer(playerUUID);

			if(player == null)
				return;

			playerAction.accept(player, playerCooldown);
		};
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
		return ifOnline((player, playerCooldown) ->
		{
			String[] finalMessages = Arrays.stream(messages)
					.map(message ->
					{
						return 	message
								.replace("%player%", player.getName())
								.replace("%time%", playerCooldown.getTimeLeft(player).map(DurationUtils::describe).get());
					})
					.toArray(String[]::new);

			player.sendMessage(finalMessages);
		});
	}
}