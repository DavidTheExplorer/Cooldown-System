package dte.cooldownsystem.tasks;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.scheduler.BukkitRunnable;

import dte.cooldownsystem.cooldown.Cooldown;
import dte.cooldownsystem.cooldown.CooldownCreationListener;
import dte.cooldownsystem.cooldownfuture.CooldownFuture;
import dte.cooldownsystem.utils.CollectionUtils;

public class WhenOverHandler extends BukkitRunnable implements CooldownCreationListener
{
	private final Map<Cooldown, Set<UUID>> lastTracked = new HashMap<>();

	@Override
	public void onCooldownCreated(Cooldown cooldown) 
	{
		//track the cooldown's players ONLY if a "when over" strategy was defined
		cooldown.whenOver().ifPresent(ignored -> this.lastTracked.put(cooldown, new HashSet<>()));
	}

	@Override
	public void run()
	{
		this.lastTracked.forEach((cooldown, trackedPlayers) -> 
		{
			CooldownFuture whenOver = cooldown.whenOver().get();
			
			//track new players
			getUntrackedPlayers(cooldown).forEach(trackedPlayers::add);
			
			//run the whenOver strategy on players who aren't on cooldown anymore
			getReleasedPlayers(cooldown).forEach(released -> 
			{
				trackedPlayers.remove(released);
				whenOver.accept(released, cooldown);
			});
		});
	}
	
	private List<UUID> getUntrackedPlayers(Cooldown cooldown)
	{
		return CollectionUtils.getDifferences(cooldown.getPlayersUUIDs(), this.lastTracked.get(cooldown));
	}
	
	private List<UUID> getReleasedPlayers(Cooldown cooldown)
	{
		return CollectionUtils.getDifferences(this.lastTracked.get(cooldown), cooldown.getPlayersUUIDs());
	}
}