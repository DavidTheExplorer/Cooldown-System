package dte.calmdown.bukkit;

import dte.calmdown.platform.scheduler.TaskScheduler;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.time.Duration;

public class BukkitTaskScheduler implements TaskScheduler
{
    private final Plugin plugin;

    public BukkitTaskScheduler(Plugin plugin)
    {
        this.plugin = plugin;
    }

    @Override
    public void scheduleEvery(Runnable task, Duration delay)
    {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this.plugin, task, 0, toTicks(delay));
    }

    private static long toTicks(Duration duration)
    {
        return duration.toMillis() / 50;
    }
}