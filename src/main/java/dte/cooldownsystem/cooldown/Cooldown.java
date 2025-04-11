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
 * Represents a period that a player is forced to wait.
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
	 * Convenient version of {@link Cooldown#put(UUID, Duration)} that directly accepts the {@code Player}.
	 */
	public void put(Player player, Duration time) 
	{
		Validate.notNull(player, "The player to put on cooldown must be provided!");
		
		put(player.getUniqueId(), time);
	}
	
	/**
	 * Puts the provided {@code player}(identified by their UUID) on this cooldown for the provided {@code time}.
	 * 
	 * @param playerUUID The UUID of the player.
	 * @param time The time.
	 */
	public void put(UUID playerUUID, Duration time) 
	{
		Validate.notNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Validate.notNull(time, "The time to put the player on cooldown must be provided!");
		
		this.endDates.put(playerUUID, Instant.now().plus(time));
	}

	/**
	 * Convenient version of {@link Cooldown#put(UUID)} that directly accepts the {@code Player}.
	 */
	public void put(Player player)
	{
		Validate.notNull(player, "The player to put on cooldown must be provided!");
		
		put(player.getUniqueId());
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
		Validate.notNull(playerUUID, "The UUID of the player to put on cooldown must be provided!");
		Validate.notNull(this.defaultTime, "Cannot put a player on cooldown for the default time, because such one wasn't set.");
		
		put(playerUUID, this.defaultTime);
	}

	/**
	 * Convenient version of {@link Cooldown#isOn(UUID)} that directly accepts the {@code Player}.
	 */
	public boolean isOn(Player player)
	{
		Validate.notNull(player, "The player to check must be provided!");
		
		return isOn(player.getUniqueId());
	}

	/**
	 * Checks whether the provided {@code player}(identified by their UUID) is on this cooldown.
	 * 
	 * @param playerUUID The UUID of the player.
	 * @return Whether the player is on cooldown.
	 */
	public boolean isOn(UUID playerUUID)
	{
		Validate.notNull(playerUUID, "The UUID of the player to check must be provided!");
		
		Instant endDate = this.endDates.getOrDefault(playerUUID, Instant.MIN);
		
		return Instant.now().isBefore(endDate);
	}
	
	/**
	 * Convenient version of {@link Cooldown#release(UUID)} that directly accepts the {@code Player}.
	 */
	public void release(Player player)
	{
		Validate.notNull(player, "The player to release must be provided!");
		
		release(player.getUniqueId());
	}

	/**
	 * Releases the provided {@code player}(identified by their UUID) from this cooldown.
	 * 
	 * @param playerUUID The UUID of the player.
	 */
	public void release(UUID playerUUID)
	{
		Validate.notNull(playerUUID, "The UUID of the player to release on cooldown must be provided!");
		
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
	 * Sets the default time to put players on this cooldown.
	 * 
	 * @param defaultTime The new default time.
	 */
	public void setDefaultTime(Duration defaultTime) 
	{
		this.defaultTime = defaultTime;
	}

	/**
	 * Convenient version of {@link Cooldown#getTimeLeft(UUID)} that directly accepts the {@code Player}
	 */
	public Optional<Duration> getTimeLeft(Player player)
	{
		return getTimeLeft(player.getUniqueId());
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
	 * Returns the default amount of time for players to be on this cooldown.
	 * 
	 * @return The default time.
	 */
	public Optional<Duration> getDefaultTime()
	{
		return Optional.ofNullable(this.defaultTime);
	}

	/**
	 * Convenient version of {@link Cooldown#isRejecting(UUID)} that directly accepts the {@code Player}.
	 */
	public boolean isRejecting(Player player) 
	{
		return isRejecting(player.getUniqueId());
	}
	
	/**
	 * If the provided {@code player}(identified by their UUID) is on this cooldown, the rejection strategy is called and true is returned.
	 * Otherwise, nothing happens and false is returned.
	 * 
	 * @param playerUUID The uuid of the potentially on cooldown player.
	 * @return Whether the player was rejected or not.
	 * @see #getRejectionStrategy()
	 */
	public boolean isRejecting(UUID playerUUID) 
	{
		Validate.notNull(this.rejectionStrategy, "The rejection strategy must be defined in case the player is on cooldown.");
		
		if(!isOn(playerUUID))
			return false;
		
		this.rejectionStrategy.accept(playerUUID, this);
		return true;
	}

	/**
	 * Returns what happens when this cooldown is over for someone.
	 * 
	 * @return What happens as an object.
	 */
	public Optional<CooldownFuture> whenOver()
	{
		return Optional.ofNullable(this.whenOver);
	}

	/**
	 * Returns what happens when this cooldown rejects someone.
	 * 
	 * @return What happens as an object.
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
	 * Sets what happens when this cooldown rejects someone.
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
	
	//removes players that aren't on this cooldown, triggering the 'whenOver' strategy if it was defined
	private void refresh()
	{
		this.endDates.keySet().removeIf(playerUUID -> 
		{
			if(isOn(playerUUID))
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
		 * Sets the default time to put players on the cooldown.
		 * 
		 * @param defaultTime The default time.
		 * @return This builder object for chaining purposes.
		 */
		public Builder withDefaultTime(Duration defaultTime) 
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
		public Builder rejectsWith(CooldownFuture rejectionStrategy) 
		{
			this.rejectionStrategy = rejectionStrategy;
			return this;
		}
		
		/**
		 * Sets what happens when the cooldown is over for someone.
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