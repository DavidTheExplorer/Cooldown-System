package dte.cooldownsystem.cooldown.future.factory;

import java.util.function.BiConsumer;

import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.cooldown.future.CooldownFuture;
import dte.cooldownsystem.cooldown.future.PlayerConsumerFuture;
import dte.cooldownsystem.cooldown.future.RejectWithMessageFuture;

public class CooldownFutureFactory 
{
	//Container of static factory methods
	private CooldownFutureFactory(){}

	//Cached Stateless Futures
	public static final CooldownFuture 
	DO_NOTHING = (player, playerCooldown) -> {},
	DEFAULT_MESSAGE = message(String.format("Your cooldown will be over in %s.", RejectWithMessageFuture.TIME_PLACEHOLDER));
	
	/**
	 * Creates a {@code CooldownFuture} that runs the provided {@code player action} on the player passed by their UUID <b>only</b> if the player is online.
	 * 
	 * @param playerAction The action to run on the online player.
	 * @return The created future.
	 */
	public static CooldownFuture ifOnline(BiConsumer<Player, Cooldown> playerAction) 
	{
		return new PlayerConsumerFuture(playerAction);
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
		return new RejectWithMessageFuture(message);
	}
}