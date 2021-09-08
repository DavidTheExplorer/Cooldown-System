package dte.cooldownsystem.cooldown;

import static java.util.stream.Collectors.toSet;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.bukkit.craftbukkit.libs.org.apache.commons.lang3.Validate;
import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldown.future.CooldownFuture;
import dte.cooldownsystem.cooldown.future.factory.CooldownFutureFactory;
import dte.cooldownsystem.cooldown.listeners.CooldownCreationListener;

/**
 * Represents a time that a player has to wait in order to repeat an action.
 */
public class Cooldown
{
	private final Map<UUID, Instant> endDates = new HashMap<>();
	private final CooldownFuture rejectionStrategy, whenOverStrategy;
	private Duration defaultTime;

	private Cooldown(Builder builder)
	{
		this.rejectionStrategy = builder.rejectionStrategy;
		this.whenOverStrategy = builder.whenOverStrategy;
		this.defaultTime = builder.defaultTime;
	}

	/**
	 * Puts the provided {@code player} on this cooldown for the provided {@code time}.
	 * 
	 * @param player the player to put on cooldown.
	 * @param duration The duration the player will be on cooldown.
	 */
	public void put(Player player, Duration time) 
	{
		Objects.requireNonNull(time, "The cooldown time must be provided!");
		
		this.endDates.put(player.getUniqueId(), Instant.now().plus(time));
	}

	/**
	 * Puts the provided {@code player} on this cooldown for the default time.
	 * 
	 * @param player The player to put on cooldown.
	 * @throws UnsupportedOperationException If a default time wasn't set for this cooldown.
	 */
	public void put(Player player) throws UnsupportedOperationException
	{
		Validate.notNull(this.defaultTime, "Cannot put a player on cooldown for the default time, because such one wasn't set.");
		
		put(player, this.defaultTime);
	}
	
	/**
	 * Returns whether the provided {@code player} is on this cooldown.
	 * 
	 * @param player The player who will be checked.
	 * @return true if the player was on cooldown, otherwise false.
	 */
	public boolean isOnCooldown(Player player)
	{
		return isOnCooldown(player.getUniqueId());
	}

	/**
	 * Checks whether the player provided by their {@code UUID} is on this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be checked.
	 * @return true if the player was on cooldown, false otherwise.
	 */
	public boolean isOnCooldown(UUID playerUUID) 
	{
		Instant endDate = this.endDates.getOrDefault(playerUUID, Instant.MIN);
		
		//if the player doesn't have a recorded time - they weren't put on cooldown
		if(endDate == null) 
			return false;
		
		return Instant.now().isBefore(endDate);
	}

	/**
	 * Deletes the player provided by their {@code UUID} from this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be removed.
	 * @return true if the player had a recorded time on this cooldown(can have a time that passed), false otherwise.
	 */
	public boolean delete(UUID playerUUID)
	{
		boolean wasOnCooldown = this.endDates.remove(playerUUID) == null;

		return wasOnCooldown;
	}

	/**
	 * Sets the time to put players on this cooldown when {@link #put(Player)} is called; Also known as the default time.
	 * 
	 * @param defaultTime The default time of this cooldown.
	 */
	public void setDefaultTime(Duration defaultTime) 
	{
		this.defaultTime = defaultTime;
	}
	
	/**
	 * Returns an Optional of the time left for provided {@code player} to be on this cooldown.
	 * 
	 * @param player The player on cooldown.
	 * @return The player's time left of cooling down.
	 */
	public Optional<Duration> getTimeLeft(Player player)
	{
		return getTimeLeft(player.getUniqueId());
	}

	/**
	 * Returns an Optional of the time left for provided player by their {@code uuid} to be on this cooldown.
	 * 
	 * @param playerUUID The uuid of player on cooldown.
	 * @return The player's time left of cooling down.
	 */
	public Optional<Duration> getTimeLeft(UUID playerUUID)
	{
		return Optional.ofNullable(this.endDates.get(playerUUID))
				.map(endDate -> Duration.between(Instant.now(), endDate));
	}
	
	public Optional<Duration> getDefaultTime()
	{
		return Optional.ofNullable(this.defaultTime);
	}
	
	/**
	 * If the provided {@code player} is not on this cooldown, nothing happens and false is returned.
	 * Otherwise, This cooldown's rejection strategy would be called on the player and this method returns true.
	 * 
	 * @param player The potentially on cooldown player.
	 * @return Whether the player was rejected or not.
	 */
	public boolean isRejecting(Player player) 
	{
		UUID playerUUID = player.getUniqueId();
		
		if(!isOnCooldown(playerUUID))
			return false;
		
		this.rejectionStrategy.accept(playerUUID, this);
		return true;
	}

	/**
	 * Returns what happens when this cooldown is over for someone.
	 * 
	 * @return What happens when this cooldown is over for someone, or null if no action was defined.
	 */
	public Optional<CooldownFuture> whenOver()
	{
		return Optional.of(this.whenOverStrategy);
	}

	/**
	 * Returns the rejection strategy of this cooldown, which is called by {@link #wasRejected(Player)} for convenience.
	 * 
	 * @return the rejection strategy of this cooldown.
	 */
	public CooldownFuture getRejectionStrategy() 
	{
		return this.rejectionStrategy;
	}

	/**
	 * Returns a snapshot of the current players on this cooldown.
	 * 
	 * @return The UUIDs of the current players on this cooldown.
	 */
	public Set<UUID> getPlayersUUIDs()
	{
		return this.endDates.keySet().stream()
				.filter(this::isOnCooldown)
				.collect(toSet());
	}

	/**
	 * Returns a snapshot of the current players on this cooldown and their remaining times.
	 * 
	 * @return The data of this cooldown represented by a map.
	 */
	public Map<UUID, Instant> toMap()
	{
		return getPlayersUUIDs().stream().collect(Collectors.toMap(Function.identity(), this.endDates::get));
	}


	public static class Builder
	{
		CooldownFuture 
		rejectionStrategy = CooldownFutureFactory.DO_NOTHING,
		whenOverStrategy = CooldownFutureFactory.DO_NOTHING;
		
		Duration defaultTime;
		
		private static final List<CooldownCreationListener> CREATION_LISTENERS = new ArrayList<>();
		
		public static void addCreationListener(CooldownCreationListener listener) 
		{
			CREATION_LISTENERS.add(listener);
		}
		
		public Builder withDefaultTime(Duration defaultTime) 
		{
			this.defaultTime = defaultTime;
			return this;
		}
		
		public Builder rejectsWith(CooldownFuture rejectionStrategy) 
		{
			this.rejectionStrategy = rejectionStrategy;
			return this;
		}
		
		public Builder whenOver(CooldownFuture whenOverStrategy) 
		{
			this.whenOverStrategy = whenOverStrategy;
			return this;
		}
		
		public Cooldown build()
		{
			Objects.requireNonNull(this.rejectionStrategy, "Can't create a cooldown without a Rejection Strategy!");
			
			Cooldown cooldown = new Cooldown(this);
			CREATION_LISTENERS.forEach(listener -> listener.onCooldownCreated(cooldown));
			
			return cooldown;
		}
	}
}