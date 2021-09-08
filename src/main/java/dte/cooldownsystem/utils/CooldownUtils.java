package dte.cooldownsystem.utils;

import java.util.UUID;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;

public class CooldownUtils
{
	/**
	 * If the provided {@code player} is not on the provided {@code cooldown}, nothing happens and false is returned.
	 * Otherwise, The cooldown's rejection strategy would be called on the player and this method returns true.
	 * 
	 * @param player The potentially on cooldown player.
	 * @return Whether the player was rejected or not.
	 */
	public static boolean rejected(Cooldown cooldown, Player player) 
	{
		UUID playerUUID = player.getUniqueId();
		
		if(!cooldown.isOnCooldown(playerUUID))
			return false;
		
		cooldown.getRejectionStrategy().accept(playerUUID, cooldown);
		return true;
	}
}
