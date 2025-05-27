package dte.cooldownsystem;

import dte.cooldownsystem.platform.UUIDFetcher;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Represents a period that a player is forced to wait.
 *
 * @param <P> The type of the player.
 */
public class Cooldown<P>
{
	private final Map<UUID, Instant> endDates = new HashMap<>();
	private final UUIDFetcher<P> uuidFetcher;
	private CooldownFuture<P> rejectionStrategy;
	private Duration defaultTime;

	private Cooldown(Builder<P> builder)
	{
		this.uuidFetcher = builder.uuidFetcher;
		this.rejectionStrategy = builder.rejectionStrategy;
		this.defaultTime = builder.defaultTime;
	}

	/**
	 * Convenient version of {@link #put(UUID, Duration)} that directly accepts the {@code player}.
	 */
	public void put(P player, Duration time)
	{
		Objects.requireNonNull(player, "The player to put on cooldown must be provided!");

		put(fetchUUID(player), time);
	}
	
	/**
	 * Puts the provided {@code player}(identified by their UUID) on this cooldown for the provided {@code time}.
	 * 
	 * @param playerUUID The UUID of the player.
	 * @param time The time.
	 */
	public void put(UUID playerUUID, Duration time) 
	{
		Objects.requireNonNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Objects.requireNonNull(time, "The time to put the player on cooldown must be provided!");
		
		this.endDates.put(playerUUID, Instant.now().plus(time));
	}

	/**
	 * Convenient version of {@link #put(UUID)} that directly accepts the {@code player}.
	 */
	public void put(P player)
	{
		Objects.requireNonNull(player, "The player to put on cooldown must be provided!");
		
		put(fetchUUID(player));
	}
	
	/**
	 * Puts the provided {@code player}(identified by their UUID) on this cooldown for the default time.
	 * If no such time was defined, an exception will be thrown.
	 *
	 * @param playerUUID The UUID of the player.
	 * @see #setDefaultTime(Duration)
	 * @see Builder#withDefaultTime(Duration)
	 */
	public void put(UUID playerUUID)
	{
		Objects.requireNonNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Objects.requireNonNull(this.defaultTime, "Cannot put a player on cooldown for the default time, because such one wasn't set.");
		
		put(playerUUID, this.defaultTime);
	}

	/**
	 * Convenient version of {@link #isOn(UUID)} that directly accepts the {@code player}.
	 */
	public boolean isOn(P player)
	{
		Objects.requireNonNull(player, "The player to check must be provided!");
		
		return isOn(fetchUUID(player));
	}

	/**
	 * Checks whether the provided {@code player}(identified by their UUID) is on this cooldown.
	 * 
	 * @param playerUUID The UUID of the player.
	 * @return Whether the player is on cooldown.
	 */
	public boolean isOn(UUID playerUUID)
	{
		Objects.requireNonNull(playerUUID, "The UUID of the player to check must be provided!");
		
		Instant endDate = this.endDates.getOrDefault(playerUUID, Instant.MIN);
		
		return Instant.now().isBefore(endDate);
	}
	
	/**
	 * Convenient version of {@link #release(UUID)} that directly accepts the {@code player}.
	 */
	public void release(P player)
	{
		Objects.requireNonNull(player, "The player to release must be provided!");
		
		release(fetchUUID(player));
	}

	/**
	 * Releases the provided {@code player}(identified by their UUID) from this cooldown.
	 * 
	 * @param playerUUID The UUID of the player.
	 */
	public void release(UUID playerUUID)
	{
		Objects.requireNonNull(playerUUID, "The UUID of the player to release must be provided!");
		
		this.endDates.remove(playerUUID);
	}

	/**
	 * Convenient version of {@link #getTimeLeft(UUID)} that directly accepts the {@code player}
	 */
	public Optional<Duration> getTimeLeft(P player)
	{
		return getTimeLeft(fetchUUID(player));
	}

	/**
	 * Returns the time left for the provided {@code player}(identified by their UUID) to be on this cooldown.
	 * If the player is not on this cooldown, an empty Optional is returned.
	 *
	 * @param playerUUID The UUID of player.
	 * @return The player's time left.
	 */
	public Optional<Duration> getTimeLeft(UUID playerUUID)
	{
		return Optional.ofNullable(this.endDates.get(playerUUID))
				.map(endDate -> Duration.between(Instant.now(), endDate));
	}

	/**
	 * Convenient version of {@link #test(UUID)} that directly accepts the {@code player}.
	 */
	public boolean test(P player)
	{
		return test(fetchUUID(player));
	}

	/**
	 * If the provided {@code player}(identified by their UUID) is on this cooldown, the rejection strategy is called and false is returned.
	 * Otherwise, nothing happens and true is returned because the player had passed the test.
	 *
	 * @param playerUUID The UUID of the player.
	 * @return Whether the player has passed the test.
	 */
	public boolean test(UUID playerUUID)
	{
		Objects.requireNonNull(this.rejectionStrategy, "The rejection strategy must be defined in case the player is on cooldown.");

		if(!isOn(playerUUID))
			return true;

		this.rejectionStrategy.accept(playerUUID, this);
		return false;
	}

	/**
	 * Removes all players from this cooldown.
	 */
	public void clear()
	{
        this.endDates.clear();
    }

	/**
	 * Returns the default amount of time for players to be on this cooldown.
	 *
	 * @return The default time.
	 */
	public Optional<Duration> getDefaultTime()
	{
		return Optional.ofNullable(this.defaultTime);
	}

	/**
	 * Sets the default time to put players on this cooldown.
	 * 
	 * @param defaultTime The new default time.
	 */
	public void setDefaultTime(Duration defaultTime) 
	{
		this.defaultTime = defaultTime;
	}

	/**
	 * Returns what happens when this cooldown rejects someone.
	 * 
	 * @return What happens as an object.
	 */
	public Optional<CooldownFuture<P>> getRejectionStrategy()
	{
		return Optional.ofNullable(this.rejectionStrategy);
	}
	
	/**
	 * Sets what happens when this cooldown rejects someone.
	 * 
	 * @param strategy The behavior to use.
	 */
	public void setRejectionStrategy(CooldownFuture<P> strategy)
	{
		this.rejectionStrategy = strategy;
	}

	/**
	 * Returns a snapshot of the current players on this cooldown and their remaining times.
	 * 
	 * @return The data of this cooldown represented by a map.
	 */
	public Map<UUID, Instant> toMap()
	{
		refresh();

		return new HashMap<>(this.endDates);
	}

	private UUID fetchUUID(P player)
	{
		UUID uuid = this.uuidFetcher.fetch(player);

		if(uuid == null)
			throw new IllegalStateException(String.format("Fetching the UUID of %s returned null!", player));

		return uuid;
	}

	private void refresh()
	{
		this.endDates.keySet().removeIf(playerUUID -> !isOn(playerUUID));
	}



	public static class Builder<P>
	{
		UUIDFetcher<P> uuidFetcher;
		CooldownFuture<P> rejectionStrategy;
		Duration defaultTime;

		/**
		 * This constructor accepts platform-specific objects in order to prevent boilerplate in the fluent interface.
		 */
		public Builder(UUIDFetcher<P> uuidFetcher)
		{
			this.uuidFetcher = uuidFetcher;
		}
		
		/**
		 * Sets the default time to put players on the cooldown.
		 * 
		 * @param defaultTime The default time.
		 * @return This builder object for chaining purposes.
		 */
		public Builder<P> withDefaultTime(Duration defaultTime)
		{
			this.defaultTime = defaultTime;
			return this;
		}
		
		/**
		 * Sets the way the cooldown will reject a player.
		 *
		 * @param rejectionStrategy The behavior to use.
		 * @return This builder object for chaining purposes.
		 */
		public Builder<P> rejectsWith(CooldownFuture<P> rejectionStrategy)
		{
			this.rejectionStrategy = rejectionStrategy;
			return this;
		}

		public Cooldown<P> build()
		{
			return new Cooldown<>(this);
		}
	}
}