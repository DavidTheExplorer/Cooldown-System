package dte.cooldownsystem.bukkit.futures;

import java.util.Arrays;

import dte.cooldownsystem.utils.DurationFormatter;
import org.bukkit.entity.Player;

import dte.cooldownsystem.Cooldown;

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
		if(playerCooldown.isOn(player))
			message = message.replace("%time%", describeTimeLeft(player, playerCooldown));

		return message.replace("%player%", player.getName());

	}

	private static String describeTimeLeft(Player player, Cooldown<Player> playerCooldown)
	{
		return playerCooldown.getTimeLeft(player)
				.map(DurationFormatter::format)
				.get();
	}
}
