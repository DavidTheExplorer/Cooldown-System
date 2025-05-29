package dte.calmdown.bukkit;

import dte.calmdown.CooldownFactory;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class BukkitCooldownFactory
{
    public static CooldownFactory<Player> createFor(Plugin plugin)
    {
        return CooldownFactory.create(new BukkitUUIDFetcher(), new BukkitTaskScheduler(plugin));
    }
}
