package dte.cooldownsystem.bukkit;

import dte.cooldownsystem.CooldownSystem;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitCooldownSystem
{
    public static CooldownSystem<Player> createFor(Plugin plugin)
    {
        return CooldownSystem.create(new BukkitUUIDFetcher(), new BukkitTaskScheduler(plugin));
    }
}
