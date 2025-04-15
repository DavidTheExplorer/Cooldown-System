package dte.cooldownsystem.bukkit.futures;

import java.util.Arrays;

import org.bukkit.entity.Player;

import dte.cooldownsystem.Cooldown;
import dte.cooldownsystem.bukkit.utils.DurationUtils;

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

	private static String injectPlaceholders(String message, Player player, Cooldown<Player> playerCooldown)
	{
		return message
				.replace("%player%", player.getName())
				.replace("%time%", describeTimeLeft(player, playerCooldown));
	}

	private static String describeTimeLeft(Player player, Cooldown<Player> playerCooldown)
	{
		return playerCooldown.getTimeLeft(player)
				.map(DurationUtils::describe)
				.get();
	}
}
