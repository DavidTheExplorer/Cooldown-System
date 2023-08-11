package dte.cooldownsystem.cooldownfuture;

import java.util.UUID;
import java.util.function.BiConsumer;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;

public class OnlinePlayerFuture implements CooldownFuture
{
	private final BiConsumer<Player, Cooldown> playerAction;

	public OnlinePlayerFuture(BiConsumer<Player, Cooldown> playerAction) 
	{
		this.playerAction = playerAction;
	}

	@Override
	public void accept(UUID playerUUID, Cooldown playerCooldown) 
	{
		Player player = Bukkit.getPlayer(playerUUID);

		if(player == null)
			return;

		this.playerAction.accept(player, playerCooldown);
	}
}
