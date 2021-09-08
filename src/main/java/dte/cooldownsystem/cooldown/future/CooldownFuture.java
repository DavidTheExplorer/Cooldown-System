package dte.cooldownsystem.cooldown.future;

import java.util.UUID;
import java.util.function.BiConsumer;

import dte.cooldownsystem.cooldown.Cooldown;

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
}