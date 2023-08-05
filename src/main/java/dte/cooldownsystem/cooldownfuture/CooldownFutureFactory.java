package dte.cooldownsystem.cooldownfuture;

import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.utils.DurationUtils;

public class CooldownFutureFactory 
{
	/**
	 * Creates a {@code CooldownFuture} that runs the provided {@code player action} on the player passed by their UUID <b>only</b> if the player is online.
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
	 * Creates a {@code CooldownFuture} that sends a message to the player, with the following Placeholders:
	 * <nl>
	 * 	<li><i>%time%</i> - the remaining time of the player.
	 * 	<li><i>%player%</i> - the player's name.
	 * </nl>
	 * 
	 * @param message The message to send to the player.
	 * @return The created messager future.
	 */
	public static CooldownFuture message(String message) 
	{
		return ifOnline((player, playerCooldown) ->
		{
			String finalMessage = replacePlaceholders(player, playerCooldown, message);
			
			player.sendMessage(finalMessage);
		});
	}
	
	private static String replacePlaceholders(Player player, Cooldown playerCooldown, String message) 
	{
		String elegantTimeLeft = playerCooldown.getTimeLeft(player)
				.map(DurationUtils::describe)
				.get();
		
		message = message.replace("%player%", player.getName());
		message = message.replace("%time%", elegantTimeLeft);
		
		return message;
	}
}