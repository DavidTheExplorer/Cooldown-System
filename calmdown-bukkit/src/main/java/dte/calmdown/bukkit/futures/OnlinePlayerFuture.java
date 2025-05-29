package dte.calmdown.bukkit.futures;

import java.util.UUID;
import java.util.function.BiConsumer;

import dte.calmdown.CooldownFuture;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dte.calmdown.Cooldown;

public class OnlinePlayerFuture implements CooldownFuture<Player>
{
	private final BiConsumer<Player, Cooldown<Player>> playerAction;

	public OnlinePlayerFuture(BiConsumer<Player, Cooldown<Player>> playerAction)
	{
		this.playerAction = playerAction;
	}

	@Override
	public void accept(UUID playerUUID, Cooldown<Player> playerCooldown)
	{
		Player player = Bukkit.getPlayer(playerUUID);

		if(player == null)
			return;

		this.playerAction.accept(player, playerCooldown);
	}
}
