package dte.cooldownsystem.cooldown.future;

import java.util.Arrays;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.utils.DurationUtils;

public class MessageFuture extends OnlinePlayerFuture
{
	public MessageFuture(String... messages)
	{
		super((player, playerCooldown) -> 
		{
			String[] finalMessages = Arrays.stream(messages)
					.map(message -> injectPlaceholders(message, player, playerCooldown))
					.toArray(String[]::new);

			player.sendMessage(finalMessages);
		});
	}

	private static String injectPlaceholders(String message, Player player, Cooldown playerCooldown) 
	{
		return message
				.replace("%player%", player.getName())
				.replace("%time%", describeTimeLeft(player, playerCooldown));
	}

	private static String describeTimeLeft(Player player, Cooldown playerCooldown) 
	{
		return playerCooldown.getTimeLeft(player)
				.map(DurationUtils::describe)
				.get();
	}
}
