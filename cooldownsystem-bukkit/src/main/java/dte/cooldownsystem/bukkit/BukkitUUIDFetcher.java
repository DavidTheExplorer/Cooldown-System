package dte.cooldownsystem.bukkit;

import dte.cooldownsystem.platform.UUIDFetcher;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BukkitUUIDFetcher implements UUIDFetcher<Player>
{
    @Override
    public UUID fetch(Player player)
    {
        return player.getUniqueId();
    }
}