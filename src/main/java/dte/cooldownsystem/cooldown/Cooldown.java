package dte.cooldownsystem.cooldown;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import dte.cooldownsystem.cooldown.future.CooldownFuture;

/**
 * Represents an arbitrary time period that a player is forced to wait.
 */
public class Cooldown
{
	private final Map<UUID, Instant> endDates = new HashMap<>();
	private CooldownFuture rejectionStrategy, whenOver;
	private Duration defaultTime;

	private static final List<Cooldown> CREATED_COOLDOWNS = new ArrayList<>();
	
	static
	{
		//refresh all cooldowns every second
		Bukkit.getScheduler().scheduleSyncRepeatingTask(JavaPlugin.getProvidingPlugin(Cooldown.class), () -> CREATED_COOLDOWNS.forEach(Cooldown::refresh), 0, 20);
	}

	private Cooldown(Builder builder)
	{
		this.rejectionStrategy = builder.rejectionStrategy;
		this.whenOver = builder.whenOver;
		this.defaultTime = builder.defaultTime;
		
		CREATED_COOLDOWNS.add(this);
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
		Validate.notNull(player, "The player to put on cooldown must be provided!");
		
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
		Validate.notNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Validate.notNull(time, "The time to put the player on cooldown must be provided!");
		
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
		Validate.notNull(player, "The player to put on cooldown must be provided!");
		
		put(player.getUniqueId());
	}
	
	/**
	 * Puts the provided {@code player}(identified by their UUID) on this cooldown for the default time.
	 * 
	 * @param playerUUID The UUID of the player to put on cooldown.
	 * @throws UnsupportedOperationException If a default time wasn't set for this cooldown.
	 * @see #setDefaultTime(Duration)
	 * @see Builder#withDefaultTime(Duration)
	 */
	public void put(UUID playerUUID) throws UnsupportedOperationException
	{
		Validate.notNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Validate.notNull(this.defaultTime, "Cannot put a player on cooldown for the default time, because such one wasn't set.");
		
		put(playerUUID, this.defaultTime);
	}
	
	/**
	 * Checks whether the provided {@code player} is on this cooldown.
	 * 
	 * @param player The player who will be checked.
	 * @return whether the player was on cooldown.
	 */
	public boolean isOnCooldown(Player player)
	{
		Validate.notNull(player, "The player to check must be provided!");
		
		return isOnCooldown(player.getUniqueId());
	}

	/**
	 * Checks whether the provided {@code player}(identified by their UUID) is on this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be checked.
	 * @return whether the player was on cooldown.
	 */
	public boolean isOnCooldown(UUID playerUUID) 
	{
		Validate.notNull(playerUUID, "The UUID of the player to check must be provided!");
		
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
		Validate.notNull(player, "The player to delete must be provided!");
		
		delete(player.getUniqueId());
	}

	/**
	 * Deletes the provided {@code player}(identified by their UUID) from this cooldown.
	 * 
	 * @param playerUUID The uuid of the player who will be removed from this cooldown.
	 */
	public void delete(UUID playerUUID)
	{
		Validate.notNull(playerUUID, "The UUID of the player to delete on cooldown must be provided!");
		
		this.endDates.remove(playerUUID);
	}

	/**
	 * Removes all players from this cooldown.
	 */
	public void clear()
	{
        this.endDates.clear();
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
	 * Returns the time left for the provided {@code player}(identified by their UUID) to be on this cooldown.
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
	 * If the provided {@code player}(identified by their UUID) is on cooldown, the rejection strategy is called and this method returns true.
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
		return Optional.ofNullable(this.whenOver);
	}

	/**
	 * Returns what happens when a player who is on cooldown is passed when {@link #isRejecting(Player)} is called.
	 * 
	 * @return the rejection strategy of this cooldown, wrapped in an Optional.
	 */
	public Optional<CooldownFuture> getRejectionStrategy() 
	{
		return Optional.ofNullable(this.rejectionStrategy);
	}
	
	/**
	 * Sets what happens when this cooldown is over for someone.
	 * 
	 * @param strategy The behavior to use.
	 */
	public void whenOver(CooldownFuture strategy) 
	{
		this.whenOver = strategy;
	}
	
	/**
	 * Sets what happens when {@link #isRejecting(Player)} is called for player who is on this cooldown.
	 * 
	 * @param strategy The behavior to use.
	 */
	public void rejectWith(CooldownFuture strategy) 
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
	
	/*
	 * The responsibilities of this method are:
	 * 1) Remove players who are not on cooldown, to avoid storing irrelevant information.
	 * 2) If a behavior was defined for when this cooldown is over, run it for who is not on cooldown.
	 */
	private void refresh()
	{
		this.endDates.keySet().removeIf(playerUUID -> 
		{
			if(isOnCooldown(playerUUID)) 
				return false;
			
			if(this.whenOver != null)
				this.whenOver.accept(playerUUID, this);
			
			return true;
		});
	}


	public static class Builder
	{
		CooldownFuture rejectionStrategy, whenOver;
		Duration defaultTime;
		
		/**
		 * Sets the time to put players on this cooldown when {@link Cooldown#put(Player)} is called; Also known as the default time.
		 * 
		 * @param defaultTime The default time of the cooldown.
		 * @return This builder object for chaining purposes.
		 */
		public Builder withDefaultTime(Duration defaultTime) 
		{
			this.defaultTime = defaultTime;
			return this;
		}
		
		/**
		 * Sets what happens when {@link Cooldown#isRejecting(Player)} is called for a player who will be the cooldown.
		 * 
		 * @param rejectionStrategy The behavior to use.
		 * @return This builder object for chaining purposes.
		 */
		public Builder rejectsWith(CooldownFuture rejectionStrategy) 
		{
			this.rejectionStrategy = rejectionStrategy;
			return this;
		}
		
		/**
		 * Sets what happens when the cooldown will be over for someone.
		 * 
		 * @param whenOver The behavior to use.
		 * @return This builder object for chaining purposes.
		 */
		public Builder whenOver(CooldownFuture whenOver) 
		{
			this.whenOver = whenOver;
			return this;
		}
		
		public Cooldown build()
		{
			return new Cooldown(this);
		}
	}
}