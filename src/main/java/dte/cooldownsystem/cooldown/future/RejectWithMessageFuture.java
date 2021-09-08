package dte.cooldownsystem.cooldown.future;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.utils.time.DurationUtils;

public class RejectWithMessageFuture extends PlayerConsumerFuture
{
	public static final String
	PLAYER_NAME_PLACEHOLDER = "%player%",
	TIME_PLACEHOLDER = "%time%";

	public RejectWithMessageFuture(String message) 
	{
		super((player, playerCooldown) -> player.sendMessage(replacePlaceholders(message, player, playerCooldown)));
	}
	
	protected static String replacePlaceholders(String message, Player player, Cooldown playerCooldown) 
	{
		String descriptiveTimeLeft = playerCooldown.getTimeLeft(player)
				.map(DurationUtils::describe)
				.get();
		
		message = message.replace(PLAYER_NAME_PLACEHOLDER, player.getName());
		message = message.replace(TIME_PLACEHOLDER, descriptiveTimeLeft);

		return message;
	}
}