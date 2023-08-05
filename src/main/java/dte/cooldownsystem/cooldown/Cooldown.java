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

import org.apache.commons.lang.Validate;
import org.bukkit.entity.Player;

import dte.cooldownsystem.cooldownfuture.CooldownFuture;

/**
 * Represents a time that a player has to wait.
 */
public class Cooldown
{
	private final Map<UUID, Instant> endDates = new HashMap<>();
	private final CooldownFuture rejectionStrategy, whenOver;
	private Duration defaultTime;

	private Cooldown(Builder builder)
	{
		this.rejectionStrategy = builder.rejectionStrategy;
		this.whenOver = builder.whenOver;
		this.defaultTime = builder.defaultTime;
	}

	public static Cooldown create() 
	{
		return new Builder().build();
	}

	/**
	 * Puts the provided {@code player} on this cooldown for the provided {@code time}.
	 * 
	 * @param player the player to put on cooldown.
	 * @param time The time the player will be on cooldown.
	 */
	public void put(Player player, Duration time) 
	{
		put(player.getUniqueId(), time);
	}
	
	/**
	 * Puts the provided {@code player}(identified by their UUID) on this cooldown for the provided {@code time}.
	 * 
	 * @param playerUUID The UUID of the player to put on cooldown.
	 * @param time The duration the player will be on cooldown.
	 */
	public void put(UUID playerUUID, Duration time) 
	{
		Objects.requireNonNull(time, "The cooldown time must be provided!");
		
		this.endDates.put(playerUUID, Instant.now().plus(time));
	}

	/**
	 * Puts the provided {@code player} on this cooldown for the default time.
	 * 
	 * @param player The player to put on cooldown.
	 * @throws UnsupportedOperationException If a default time wasn't set for this cooldown.
	 * @see #setDefaultTime(Duration)
	 * @see Builder#withDefaultTime(Duration)
	 */
	public void put(Player player) throws UnsupportedOperationException
	{
		put(player.getUniqueId());
	}
	
	/**
	 * Puts the provided {@code player}(identified by their uuid) on this cooldown for the default time.
	 * 
	 * @param playerUUID The UUID of the player to put on cooldown.
	 * @throws UnsupportedOperationException If a default time wasn't set for this cooldown.
	 * @see #setDefaultTime(Duration)
	 * @see Builder#withDefaultTime(Duration)
	 */
	public void put(UUID playerUUID) 
	{
		Validate.notNull(this.defaultTime, "Cannot put a player on cooldown for the default time, because such one wasn't set.");
		
		put(playerUUID, this.defaultTime);
	}
	
	/**
	 * Returns whether the provided {@code player} is on this cooldown.
	 * 
	 * @param player The player who will be checked.
	 * @return whether the player was on cooldown.
	 */
	public boolean isOnCooldown(Player player)
	{
		return isOnCooldown(player.getUniqueId());
	}

	/**
	 * Checks whether the {@code player}(identified by their uuid) is on this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be checked.
	 * @return whether the player was on cooldown.
	 */
	public boolean isOnCooldown(UUID playerUUID) 
	{
		Instant endDate = this.endDates.getOrDefault(playerUUID, Instant.MIN);
		
		return Instant.now().isBefore(endDate);
	}
	
	/**
	 * Deletes the provided {@code player} from this cooldown.
	 * 
	 * @param player The player who will be removed from this cooldown.
	 */
	public void delete(Player player) 
	{
		delete(player.getUniqueId());
	}

	/**
	 * Deletes the {@code player}(identified by their uuid) from this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be removed from this cooldown.
	 */
	public void delete(UUID playerUUID)
	{
		this.endDates.remove(playerUUID);
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
	 * Returns the time left for provided {@code player} to be on this cooldown.
	 * 
	 * @param player The player on cooldown.
	 * @return The player's time left of cooling down(Empty Optional is returned if the player wasn't on cooldown)
	 */
	public Optional<Duration> getTimeLeft(Player player)
	{
		return getTimeLeft(player.getUniqueId());
	}

	/**
	 * Returns the time left for the {@code player}(identified by their uuid) to be on this cooldown.
	 * 
	 * @param playerUUID The uuid of player on cooldown.
	 * @return The player's time left of cooling down(Empty Optional is returned if the player wasn't on cooldown)
	 */
	public Optional<Duration> getTimeLeft(UUID playerUUID)
	{
		return Optional.ofNullable(this.endDates.get(playerUUID))
				.map(endDate -> Duration.between(Instant.now(), endDate));
	}
	
	/**
	 * Returns this cooldown's <i>default time</i> which is used when {@link #put(Player)} is called.
	 * 
	 * @return The default time of this cooldown.
	 */
	public Optional<Duration> getDefaultTime()
	{
		return Optional.ofNullable(this.defaultTime);
	}
	
	/**
	 * If the provided {@code player} is on cooldown, the rejection strategy is called and this method returns true.
	 * Otherwise, nothing happens and false is returned.
	 * 
	 * @param player The potentially on cooldown player.
	 * @return Whether the player was rejected or not.
	 * @see #getRejectionStrategy()
	 */
	public boolean isRejecting(Player player) 
	{
		return isRejecting(player.getUniqueId());
	}
	
	/**
	 * If the {@code player}(identified by their uuid) is on cooldown, the rejection strategy is called and this method returns true.
	 * Otherwise, nothing happens and false is returned.
	 * 
	 * @param playerUUID The uuid of the potentially on cooldown player.
	 * @return Whether the player was rejected or not.
	 * @see #getRejectionStrategy()
	 */
	public boolean isRejecting(UUID playerUUID) 
	{
		Validate.notNull(this.rejectionStrategy, "The rejection strategy must be defined in case the player is on cooldown.");
		
		if(!isOnCooldown(playerUUID))
			return false;
		
		this.rejectionStrategy.accept(playerUUID, this);
		return true;
	}

	/**
	 * Returns what happens when this cooldown is over for someone.
	 * 
	 * @return What happens when this cooldown is over for someone, wrapped in an Optional.
	 */
	public Optional<CooldownFuture> whenOver()
	{
		return Optional.of(this.whenOver);
	}

	/**
	 * Returns what happens when a player who is on cooldown is passed when {@link #wasRejected(Player)} is called.
	 * 
	 * @return the rejection strategy of this cooldown, wrapped in an Optional.
	 */
	public Optional<CooldownFuture> getRejectionStrategy() 
	{
		return Optional.ofNullable(this.rejectionStrategy);
	}

	/**
	 * Returns a snapshot of the players on this cooldown.
	 * 
	 * @return The UUIDs of the players on this cooldown.
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
		CooldownFuture rejectionStrategy, whenOver;
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
		
		public Builder whenOver(CooldownFuture whenOver) 
		{
			this.whenOver = whenOver;
			return this;
		}
		
		public Cooldown build()
		{
			Cooldown cooldown = new Cooldown(this);
			CREATION_LISTENERS.forEach(listener -> listener.onCooldownCreated(cooldown));
			
			return cooldown;
		}
	}
}