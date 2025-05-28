package dte.calmdown;

import java.util.UUID;

/**
 * Represents a future action that handles a player and their cooldown.
 */
@FunctionalInterface
public interface CooldownFuture<P>
{
	/**
	 * Executes on the provided {@code player}(identified by their UUID) and their {@code cooldown}.
	 * 
	 * @param playerUUID The uuid of player.
	 * @param playerCooldown The player's cooldown.
	 */
	void accept(UUID playerUUID, Cooldown<P> playerCooldown);
}